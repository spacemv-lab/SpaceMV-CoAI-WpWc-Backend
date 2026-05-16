package com.txwx.social.dashboard.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.ruoyi.common.clickhouse.service.ClickhouseService;
import com.txwx.social.dashboard.domain.entity.DwsUsers;
import com.txwx.social.dashboard.domain.vo.ImportResultVo;
import com.txwx.social.dashboard.domain.vo.RowErrorVo;
import com.txwx.social.dashboard.exception.ImportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ImportUtil 单元测试
 * <p>
 * 测试场景：
 * 1. 空文件导入 - 返回 count=0
 * 2. 成功导入（无重复） - 成功插入，返回 count=N
 * 3. 成功导入（有跳过） - 跳过重复数据
 * 4. 数据校验错误 - 抛出 ImportException
 * 5. 数据格式转换异常 - 抛出 ImportException
 * 6. ref_date 为空 - 正常导入
 * 7. accountId 为空 - 抛出 RuntimeException
 * 8. extractTableName - 表名提取
 * 9. exportErrorList - 错误数据导出
 */
@ExtendWith(MockitoExtension.class)
class ImportUtilTest {

    @InjectMocks
    private ImportUtil importUtil;

    @Mock
    private ClickhouseService clickhouseService;

    private static final String TEST_SQL = "INSERT INTO dws_users (account_id, ref_date, new_user, cancel_user, cumulate_user, create_time) VALUES (?, ?, ?, ?, ?, ?)";
    private static final Long TEST_ACCOUNT_ID = 13L;
    private static final Map<String, Object> TEST_EXT_INFO = Collections.singletonMap("accountId", TEST_ACCOUNT_ID);

    /** 预先生成的有效 xlsx 文件（含表头，0 行数据） */
    private MultipartFile testFile;

    /**
     * 所有需要读取 Excel 的用例共享一个有效的 xlsx 文件，
     * 避免 MockedStatic 与 EasyExcel 内部文件类型检测冲突。
     */
    @BeforeEach
    void setUp() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        EasyExcel.write(baos, DwsUsers.class).sheet("test").doWrite(Collections.emptyList());
        testFile = new MockMultipartFile(
                "file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );
    }

    /**
     * 创建一个 multipart file（使用 @BeforeEach 生成的有效 xlsx 文件），
     * 用于需要 MockedStatic mock 的测试用例。
     */
    private MultipartFile createMockFile() {
        return testFile;
    }

    /**
     * 测试场景 1：空文件导入
     */
    @Test
    void testImportExcel_EmptyFile() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        EasyExcel.write(baos, DwsUsers.class).sheet("test").doWrite(Collections.emptyList());

        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );

        ImportResultVo result = importUtil.importExcel(file, DwsUsers.class, TEST_SQL, null, TEST_EXT_INFO);

        assertEquals(0, result.getCount(), "成功导入行数应为 0");
        assertEquals(0, result.getSkippedCount(), "跳过行数应为 0");
    }

    /**
     * 测试场景 2：成功导入（无重复数据）
     */
    @Test
    @SuppressWarnings("unchecked")
    void testImportExcel_Success() throws Exception {
        DwsUsers mockRow = mock(DwsUsers.class);
        doNothing().when(mockRow).validate();
        when(mockRow.getRefDateStr()).thenReturn("2024-01-01");
        when(mockRow.toObject(TEST_ACCOUNT_ID))
                .thenReturn(new Object[]{13L, "2024-01-01", 10L, 2L, 10L, null});

        ExcelReaderBuilder mockBuilder = mock(ExcelReaderBuilder.class);
        ExcelReaderSheetBuilder mockSheetBuilder = mock(ExcelReaderSheetBuilder.class);
        when(mockBuilder.sheet()).thenReturn(mockSheetBuilder);

        ThreadLocal<ReadListener<DwsUsers>> listenerHolder = new ThreadLocal<>();

        try (MockedStatic<EasyExcel> mocked = mockStatic(EasyExcel.class)) {
            // 显式 stub，捕获 listener → mockBuilder
            when(EasyExcel.read(any(InputStream.class), eq(DwsUsers.class),
                    any(ReadListener.class))).thenAnswer(invocation -> {
                ReadListener<DwsUsers> listener = invocation.getArgument(2);
                listenerHolder.set(listener);
                return mockBuilder;
            });

            doAnswer(invocation -> {
                ReadListener<DwsUsers> listener = listenerHolder.get();
                if (listener != null) {
                    listener.invoke(mockRow, null);
                    listener.doAfterAllAnalysed(null);
                }
                return null;
            }).when(mockSheetBuilder).doRead();

            doNothing().when(clickhouseService).batchInsert(anyString(), anyList());

            ImportResultVo result = importUtil.importExcel(createMockFile(), DwsUsers.class, TEST_SQL, null, TEST_EXT_INFO);

            assertEquals(1, result.getCount(), "成功导入行数应为 1");
            assertEquals(0, result.getSkippedCount(), "跳过行数应为 0");
            verify(clickhouseService, times(1)).batchInsert(eq(TEST_SQL), anyList());
        }
    }

    /**
     * 测试场景 3：成功导入但有重复数据被跳过
     */
    @Test
    @SuppressWarnings("unchecked")
    void testImportExcel_SkipDuplicates() throws Exception {
        DwsUsers mockRow1 = mock(DwsUsers.class);
        doNothing().when(mockRow1).validate();
        when(mockRow1.getRefDateStr()).thenReturn("2024-01-01");

        DwsUsers mockRow2 = mock(DwsUsers.class);
        doNothing().when(mockRow2).validate();
        when(mockRow2.getRefDateStr()).thenReturn("2024-01-01");

        // readData 返回已存在的 ref_date，用来验证去重逻辑
        when(clickhouseService.readData(anyString())).thenReturn(
                Collections.singletonList(Collections.singletonMap("ref_date", "2024-01-01"))
        );

        ExcelReaderBuilder mockBuilder = mock(ExcelReaderBuilder.class);
        ExcelReaderSheetBuilder mockSheetBuilder = mock(ExcelReaderSheetBuilder.class);
        when(mockBuilder.sheet()).thenReturn(mockSheetBuilder);

        ThreadLocal<ReadListener<DwsUsers>> listenerHolder = new ThreadLocal<>();

        try (MockedStatic<EasyExcel> mocked = mockStatic(EasyExcel.class)) {
            when(EasyExcel.read(any(InputStream.class), eq(DwsUsers.class),
                    any(ReadListener.class))).thenAnswer(invocation -> {
                ReadListener<DwsUsers> listener = invocation.getArgument(2);
                listenerHolder.set(listener);
                return mockBuilder;
            });

            doAnswer(invocation -> {
                ReadListener<DwsUsers> listener = listenerHolder.get();
                if (listener != null) {
                    listener.invoke(mockRow1, null);
                    listener.invoke(mockRow2, null);
                    listener.doAfterAllAnalysed(null);
                }
                return null;
            }).when(mockSheetBuilder).doRead();

            ImportResultVo result = importUtil.importExcel(createMockFile(), DwsUsers.class, TEST_SQL, null, TEST_EXT_INFO);

            assertEquals(0, result.getCount(), "成功导入行数应为 0（全部跳过）");
            assertEquals(2, result.getSkippedCount(), "跳过行数应为 2");
        }
    }

    /**
     * 测试场景 4：数据校验错误
     * <p>
     * 修复说明：使用 @BeforeEach 预先生成有效的 xlsx 文件，避免 MockedStatic
     * 与 EasyExcel 内部类型检测冲突。同时用 when(...).thenReturn()
     * 显式 stub 替代 lambda Answer，确保 mock 稳定拦截。
     */
    @Test
    @SuppressWarnings("unchecked")
    void testImportExcel_DataValidationError() throws Exception {
        // 1. 准备 mock 数据行
        DwsUsers badRow = mock(DwsUsers.class);
        doThrow(new RuntimeException("new_user 不能为空")).when(badRow).validate();

        // 2. Stub EasyExcel.read() → mockBuilder（与 sheet() 链式调用）
        ExcelReaderBuilder mockBuilder = mock(ExcelReaderBuilder.class);
        ExcelReaderSheetBuilder mockSheetBuilder = mock(ExcelReaderSheetBuilder.class);
        when(mockBuilder.sheet()).thenReturn(mockSheetBuilder);

        ThreadLocal<ReadListener<DwsUsers>> listenerHolder = new ThreadLocal<>();

        try (MockedStatic<EasyExcel> mocked = mockStatic(EasyExcel.class)) {

            // 显式 stub read(), 捕获 listener
            when(EasyExcel.read(any(InputStream.class), eq(DwsUsers.class),
                    any(ReadListener.class))).thenAnswer(invocation -> {
                ReadListener<DwsUsers> listener = invocation.getArgument(2);
                listenerHolder.set(listener);
                return mockBuilder;
            });

            // 3. 手动触发 listener.invoke() + doAfterAllAnalysed()
            doAnswer(invocation -> {
                ReadListener<DwsUsers> listener = listenerHolder.get();
                if (listener != null) {
                    listener.invoke(badRow, null);
                    listener.doAfterAllAnalysed(null);
                }
                return null;
            }).when(mockSheetBuilder).doRead();

            // 4. 执行被测方法
            ImportResultVo result = importUtil.importExcel(testFile, DwsUsers.class, TEST_SQL, null, TEST_EXT_INFO);

            // 5. 验证结果
            assertTrue(result.getErrors().contains("1 条错误"), "错误消息应包含错误数量");
            assertEquals(1, result.getErrors().size(), "错误详情应包含 1 条");
            assertEquals("new_user 不能为空", result.getErrors().get(0).getErrorMessage(), "错误描述应正确");
        }
    }

    /**
     * 测试场景 5：数据格式转换异常
     */
    @Test
    @SuppressWarnings("unchecked")
    void testImportExcel_DataConvertException() throws Exception {
        ExcelReaderBuilder mockBuilder = mock(ExcelReaderBuilder.class);
        ExcelReaderSheetBuilder mockSheetBuilder = mock(ExcelReaderSheetBuilder.class);
        when(mockBuilder.sheet()).thenReturn(mockSheetBuilder);

        ThreadLocal<ReadListener<DwsUsers>> listenerHolder = new ThreadLocal<>();

        try (MockedStatic<EasyExcel> mocked = mockStatic(EasyExcel.class)) {
            when(EasyExcel.read(any(InputStream.class), eq(DwsUsers.class),
                    any(ReadListener.class))).thenAnswer(invocation -> {
                ReadListener<DwsUsers> listener = invocation.getArgument(2);
                listenerHolder.set(listener);
                return mockBuilder;
            });

            doAnswer(invocation -> {
                ReadListener<DwsUsers> listener = listenerHolder.get();
                if (listener != null) {
                    ExcelDataConvertException convertEx = mock(ExcelDataConvertException.class);
                    when(convertEx.getRowIndex()).thenReturn(1);
                    when(convertEx.getColumnIndex()).thenReturn(0);
                    try {
                        listener.onException(convertEx, null);
                    } catch (Exception e) {
                        // 忽略
                    }
                }
                return null;
            }).when(mockSheetBuilder).doRead();

            ImportResultVo result = importUtil.importExcel(createMockFile(), DwsUsers.class, TEST_SQL, null, TEST_EXT_INFO);

            assertTrue(result.getErrors().contains("1 条错误"));
            assertEquals(1, result.getErrors().size());
            assertEquals("第2行，第1列数据格式错误", result.getErrors().get(0).getErrorMessage());
        }
    }

    /**
     * 测试场景 6：ref_date 为空
     */
    @Test
    @SuppressWarnings("unchecked")
    void testImportExcel_NullRefDate() throws Exception {
        DwsUsers mockRow = mock(DwsUsers.class);
        doNothing().when(mockRow).validate();
        when(mockRow.getRefDateStr()).thenReturn(null);
        when(mockRow.toObject(TEST_ACCOUNT_ID))
                .thenReturn(new Object[]{13L, null, 10L, 2L, 10L, null});

        ExcelReaderBuilder mockBuilder = mock(ExcelReaderBuilder.class);
        ExcelReaderSheetBuilder mockSheetBuilder = mock(ExcelReaderSheetBuilder.class);
        when(mockBuilder.sheet()).thenReturn(mockSheetBuilder);

        ThreadLocal<ReadListener<DwsUsers>> listenerHolder = new ThreadLocal<>();

        try (MockedStatic<EasyExcel> mocked = mockStatic(EasyExcel.class)) {
            when(EasyExcel.read(any(InputStream.class), eq(DwsUsers.class),
                    any(ReadListener.class))).thenAnswer(invocation -> {
                ReadListener<DwsUsers> listener = invocation.getArgument(2);
                listenerHolder.set(listener);
                return mockBuilder;
            });

            doAnswer(invocation -> {
                ReadListener<DwsUsers> listener = listenerHolder.get();
                if (listener != null) {
                    listener.invoke(mockRow, null);
                    listener.doAfterAllAnalysed(null);
                }
                return null;
            }).when(mockSheetBuilder).doRead();

            doNothing().when(clickhouseService).batchInsert(anyString(), anyList());

            ImportResultVo result = importUtil.importExcel(createMockFile(), DwsUsers.class, TEST_SQL, null, TEST_EXT_INFO);

            assertEquals(1, result.getCount());
            assertEquals(0, result.getSkippedCount());
        }
    }

    /**
     * 测试场景 7：accountId 为空
     */
    @Test
    void testImportExcel_AccountIdIsNull() throws Exception {
        Map<String, Object> extInfo = new HashMap<>();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            importUtil.importExcel(createMockFile(), DwsUsers.class, TEST_SQL, null, extInfo);
        });

        assertTrue(ex.getMessage().contains("账号id为空"));
    }

    /**
     * 测试场景 8a：extractTableName - 有效 SQL
     */
    @Test
    void testExtractTableName_Valid() throws Exception {
        String result = (String) ReflectionTestUtils.invokeMethod(
                importUtil, "extractTableName",
                "INSERT INTO dws_users (account_id, ref_date) VALUES (?, ?)"
        );
        assertEquals("dws_users", result);
    }

    /**
     * 测试场景 8b：extractTableName - 无效 SQL
     */
    @Test
    void testExtractTableName_Invalid() throws Exception {
        String result = (String) ReflectionTestUtils.invokeMethod(
                importUtil, "extractTableName",
                "SELECT * FROM users"
        );
        assertNull(result);
    }

    /**
     * 测试场景 9a：exportErrorList - 无原始行数据
     */
    @Test
    void testExportErrorList_EmptyRowData() throws Exception {
        List<RowErrorVo> errors = new ArrayList<>();
        errors.add(new RowErrorVo<>(2L, "ref_date 格式错误", null));

        byte[] result = importUtil.exportErrorList(DwsUsers.class, errors, TEST_EXT_INFO);

        assertNotNull(result);
        assertTrue(result.length > 0, "Excel 数据不应为空");
        assertEquals(0x50, result[0], "XLSX 文件头应为 0x50");
        assertEquals(0x4B, result[1], "XLSX 文件头应为 0x4B");
    }

    /**
     * 测试场景 9b：exportErrorList - 有原始行数据
     */
    @Test
    void testExportErrorList_WithRowData() throws Exception {
        DwsUsers rowData = mock(DwsUsers.class);
        when(rowData.toObject(TEST_ACCOUNT_ID))
                .thenReturn(new Object[]{13L, "2024-01-01", 10L, 2L, 10L, null});

        List<RowErrorVo> errors = new ArrayList<>();
        errors.add(new RowErrorVo<>(2L, "ref_date 格式错误", rowData));

        byte[] result = importUtil.exportErrorList(DwsUsers.class, errors, TEST_EXT_INFO);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(0x50, result[0]);
        assertEquals(0x4B, result[1]);
    }
}
