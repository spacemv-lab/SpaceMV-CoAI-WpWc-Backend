package com.txwx.social.dashboard.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.txwx.social.dashboard.exception.ImportException;
import com.txwx.social.dashboard.mapper.IImportBaseModel;
import com.txwx.social.dashboard.domain.vo.ImportResultVo;
import com.txwx.social.dashboard.domain.vo.RowErrorVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ImportUtil {

    private static final Logger log = LoggerFactory.getLogger(ImportUtil.class);

    // 每批数据量
    private static final int BATCH_SIZE = 10000;

    @Autowired
    private ClickhouseService clickhouseService;

    /**
     * 从INSERT SQL中提取表名
     * 例如: INSERT INTO table_name (col1, col2) VALUES (?, ?) -> table_name
     */
    private String extractTableName(String sql) {
        if (sql == null || !sql.toUpperCase().startsWith("INSERT")) {
            return null;
        }
        String upperSql = sql.toUpperCase();
        int intoIndex = upperSql.indexOf("INTO");
        if (intoIndex == -1) {
            return null;
        }
        String afterInto = sql.substring(intoIndex + 4).trim();
        // 表名可能包含反引号或空格
        String tableName = afterInto.split("[\\s(`]")[0];
        return tableName;
    }

    /**
     * 查询指定表和accountId下已存在的ref_date集合
     */
    private Set<String> queryExistingRefDates(String tableName, Long accountId) {
        Set<String> existingRefDates = new HashSet<>();
        try {
            String querySql = String.format("SELECT DISTINCT ref_date FROM %s WHERE account_id = %d",
                    tableName, accountId);
            List<Map<String, Object>> results = clickhouseService.readData(querySql);
            for (Map<String, Object> row : results) {
                Object refDateObj = row.get("ref_date");
                if (refDateObj != null) {
                    existingRefDates.add(refDateObj.toString());
                }
            }
            log.info("查询到表 {} 中account_id={} 已存在的ref_date数量: {}",
                    tableName, accountId, existingRefDates.size());
        } catch (Exception e) {
            log.warn("查询已存在ref_date失败，将不进行去重检查: {}", e.getMessage());
        }
        return existingRefDates;
    }

    public <T extends IImportBaseModel> ImportResultVo importExcel(
            MultipartFile file,
            Class<T> clazz,
            String sql,
            Consumer<T> validator,
            Map<String, Object> extInfo) throws Exception {

        List<RowErrorVo> errors = new ArrayList<>();
        List<Object[]> batchArgs = new ArrayList<>(BATCH_SIZE);
        Long accountId = (Long) extInfo.get("accountId");
        if (accountId == null) {
            throw new RuntimeException("传入账号id为空，请检查");
        }

        // 1. 查询已存在的ref_date（用于去重）
        String tableName = extractTableName(sql);
        Set<String> existingRefDates;
        Set<String> processedRefDatesInFile = new HashSet<>(); // 记录当前文件中已处理的ref_date
        final int[] skippedCount = {0}; // 跳过的数据行数

        if (tableName != null) {
            existingRefDates = queryExistingRefDates(tableName, accountId);
        } else {
            existingRefDates = new HashSet<>();
            log.warn("无法从SQL中提取表名，将不进行ref_date去重检查");
        }

        // 2. 开始流式读取
        EasyExcel.read(file.getInputStream(), clazz, new AnalysisEventListener<T>() {
            long rowNo = 1;

            @Override
            public void invoke(T row, AnalysisContext context) {
                rowNo++;
                try {
                    // 实体类内部校验
                    row.validate();
                    // 外部自定义校验
                    if (validator != null) validator.accept(row);

                    // ref_date去重检查
                    String refDateStr = row.getRefDateStr();
                    if (refDateStr != null) {
                        // 检查数据库中是否已存在
                        if (existingRefDates.contains(refDateStr)) {
                            log.warn("跳过重复数据: ref_date={}, account_id={}, 原因: 数据库中已存在",
                                    refDateStr, accountId);
                            skippedCount[0]++;
                            return; // 跳过此行
                        }
                        // 检查当前文件中是否已处理过（处理文件内重复）
                        if (processedRefDatesInFile.contains(refDateStr)) {
                            log.warn("跳过重复数据: ref_date={}, account_id={}, 原因: 当前文件中已存在",
                                    refDateStr, accountId);
                            skippedCount[0]++;
                            return; // 跳过此行
                        }
                    }

                    batchArgs.add(row.toObject(accountId));

                    // 记录已处理的ref_date
                    if (refDateStr != null) {
                        processedRefDatesInFile.add(refDateStr);
                        existingRefDates.add(refDateStr); // 避免同一批中后续重复
                    }

                    if (batchArgs.size() >= BATCH_SIZE) {
                        flush();
                    }
                } catch (Exception ex) {
                    // 关键：记录错误行号、原因及原始对象
                    errors.add(new RowErrorVo(rowNo, ex.getMessage(), row));
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                flush();
            }

            private void flush() {
                if (!batchArgs.isEmpty()) {
                    clickhouseService.batchInsert(sql, batchArgs);
                    log.info("批量插入数据: {} 条", batchArgs.size());
                    batchArgs.clear();
                }
            }

            @Override
            public void onException(Exception exception, AnalysisContext context) {
                // 如果是数据转换异常（比如：中文转数字）
                if (exception instanceof ExcelDataConvertException) {
                    ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
                    String errorMsg = String.format("第%s行，第%s列数据格式错误",
                            excelDataConvertException.getRowIndex() + 1,
                            excelDataConvertException.getColumnIndex() + 1);

                    // 将转换错误加入错误列表
                    errors.add(new RowErrorVo((long)excelDataConvertException.getRowIndex() + 1, errorMsg, null));
                } else {
                    // 其他异常（如读取中断）直接抛出
                    throw new RuntimeException(exception);
                }
            }
        }).sheet().doRead();

        // 3. 记录去重结果
        if (skippedCount[0] > 0) {
            log.info("导入完成: 跳过 {} 条重复数据 (ref_date已存在)", skippedCount[0]);
        }

        // 4. 处理结果：如果有错误，抛出异常让 Controller 处理
        if (!errors.isEmpty()) {
            throw new ImportException("导入存在 " + errors.size() + " 条错误数据，请下载错误文件查看", errors);
        }

        return ImportResultVo.of(batchArgs.size(), skippedCount[0], Collections.emptyList());
    }

    /**
     * 将错误数据导出为 Excel 的 byte[]，供 Controller 写入 response
     */
    public <T extends IImportBaseModel> byte[] exportErrorList(
            Class<T> clazz,
            List<RowErrorVo> errors,
            Map<String, Object> extInfo) throws IOException {

        Long accountId = (Long) extInfo.get("accountId");
        if (accountId == null) {
            throw new RuntimeException("账号ID为空");
        }

        // 2. 动态构建表头 (读取 @ExcelProperty 的值 + 增加"错误原因"）
        List<List<String>> head = new ArrayList<>();
        List<Field> excelFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(ExcelProperty.class))
                .collect(Collectors.toList());
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (excelProperty != null) {
                head.add(Collections.singletonList(excelProperty.value()[0]));
            }
        }
        // 在末尾强行插入一列
        head.add(Collections.singletonList("错误原因"));
        // 业务数据的列数
        int columnCount = excelFields.size();
        // 3. 动态构建数据 (利用已有的 toObject)
        List<List<Object>> dataList = new ArrayList<>();
        for (RowErrorVo error : errors) {
            T rowData = (T) error.getRowData();
            List<Object> rowDataList = new ArrayList<>();
            if (rowData != null) {
                // 情况 A：数据转换成功但校验失败，有原始数据
                Object[] originArray = rowData.toObject(accountId);
                for (Object obj : originArray) {
                    if (obj == null) {
                        rowDataList.add("");
                    } else if (obj instanceof LocalDate || obj instanceof LocalDateTime) {
                        // 转为字符串，保证兼容性
                        rowDataList.add(obj.toString());
                    } else {
                        rowDataList.add(obj);
                    }
                }
            } else {
                // 情况 B：数据转换就失败了（如填了中文），rowData 是空的
                // 我们填充 columnCount 个空位，保证"错误原因"在最后
                for (int i = 0; i < columnCount; i++) {
                    // 或者填充 "数据解析失败"
                    rowDataList.add("数据解析失败");
                }
            }
            // 把错误信息塞到最后
            rowDataList.add(error.getErrorMessage());
            dataList.add(rowDataList);
        }

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        EasyExcel.write(outputStream)
                .head(head)
                .sheet("失败明细")
                .doWrite(dataList);
        return outputStream.toByteArray();
    }
}
