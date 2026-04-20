package com.txwx.social.dashboard.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.txwx.social.dashboard.mapper.IImportBaseModel;
import com.txwx.social.dashboard.domain.vo.ImportResultVo;
import com.txwx.social.dashboard.domain.vo.RowErrorVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
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

    // 每批数据量
    private static final int BATCH_SIZE = 10000;

    @Autowired
    private ClickhouseService clickhouseService;


    public <T extends IImportBaseModel> ImportResultVo importExcel(
            MultipartFile file,
            Class<T> clazz,
            String sql,
            HttpServletResponse response,
            Consumer<T> validator,
            Map<String, Object> extInfo) throws Exception {

        List<RowErrorVo> errors = new ArrayList<>();
        List<Object[]> batchArgs = new ArrayList<>(BATCH_SIZE);
        Long accountId = (Long) extInfo.get("accountId");
        if (accountId == null) {
            throw new RuntimeException("传入账号id为空，请检查");
        }
        // 1. 开始流式读取
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

                    batchArgs.add(row.toObject(accountId));
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

        // 2. 处理结果：如果有错误，触发下载
        if (!errors.isEmpty()) {
            exportErrorList(response, clazz, errors, extInfo);
            // 返回错误统计
            return ImportResultVo.of(errors.size(), errors);
        }

        return ImportResultVo.of(batchArgs.size(), Collections.emptyList());
    }

    private <T extends IImportBaseModel> void exportErrorList(
            HttpServletResponse response,
            Class<T> clazz,
            List<RowErrorVo> errors,
            Map<String, Object> extInfo) throws IOException {

        Long accountId = (Long) extInfo.get("accountId");
        if (accountId == null) {
            throw new RuntimeException("账号ID为空");
        }
        response.reset();
        // 1. 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String rawFileName = "导入失败记录_" + System.currentTimeMillis();
        String encodedFileName = URLEncoder.encode(rawFileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName + ".xlsx");
        // 2. 动态构建表头 (读取 @ExcelProperty 的值 + 增加“错误原因”)
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
                // 我们填充 columnCount 个空位，保证“错误原因”在最后
                for (int i = 0; i < columnCount; i++) {
                    // 或者填充 "数据解析失败"
                    rowDataList.add("数据解析失败");
                }
            }
            // 把错误信息塞到最后
            rowDataList.add(error.getErrorMessage());
            dataList.add(rowDataList);
        }

        EasyExcel.write(response.getOutputStream())
                .head(head)
                .sheet("失败明细")
                .doWrite(dataList);
    }
}

