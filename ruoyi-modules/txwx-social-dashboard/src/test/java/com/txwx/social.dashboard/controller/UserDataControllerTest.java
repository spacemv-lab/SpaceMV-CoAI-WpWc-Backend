package com.txwx.social.dashboard.controller;

import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.dashboard.config.WebChatConfig;
import com.txwx.social.dashboard.domain.entity.DwsUsers;
import com.txwx.social.dashboard.domain.vo.ImportResultVo;
import com.txwx.social.dashboard.domain.vo.RowErrorVo;
import com.txwx.social.dashboard.exception.ImportException;
import com.txwx.social.dashboard.service.IDwsUsersService;
import com.txwx.social.dashboard.util.ImportUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserDataController 单元测试
 * <p>
 * 测试场景：
 * 1. 导入成功 - 返回 JSON 成功响应
 * 2. 导入成功但有跳过数据 - 返回 JSON 提示跳过了重复数据
 * 3. 导入失败（数据校验错误）- 返回 JSON 错误响应（含错误详情）
 * 4. 导入失败（运行时异常）- 异常向上抛出，由全局异常处理器处理
 *
 * @author txwx
 */
@ExtendWith(MockitoExtension.class)
public class UserDataControllerTest {

    @InjectMocks
    private UserDataController userDataController;

    @Mock
    private ImportUtil importUtil;

    @Mock
    private WebChatConfig webChatConfig;

    @Mock
    private IDwsUsersService iDwsUsersService;

    private static final Long TEST_ACCOUNT_ID = 13L;
    private static final String TEST_SQL = "INSERT INTO dws_users (account_id, ref_date, new_user, cancel_user, cumulate_user, create_time) VALUES (?, ?, ?, ?, ?, ?)";

    @BeforeEach
    void setUp() {
        when(webChatConfig.getInsertdwsuserssql()).thenReturn(TEST_SQL);
    }

    /**
     * 测试场景 1：导入成功
     * 期望：返回 AjaxResult，code=200，msg="导入成功!"
     */
    @Test
    void testImportExcel_Success() throws Exception {
        // 准备：模拟导入成功
        ImportResultVo successResult = ImportResultVo.of(100, 0, Collections.emptyList());
        when(importUtil.importExcel(any(), eq(DwsUsers.class), eq(TEST_SQL), isNull(), anyMap()))
                .thenReturn(successResult);

        // 执行
        MockMultipartFile file = createValidExcelFile();

        AjaxResult result = userDataController.importExcel(file, TEST_ACCOUNT_ID);

        // 验证：返回 AjaxResult 成功响应
        assertNotNull(result, "返回结果不应为空");
        assertEquals(200, result.get("code"), "状态码应为 200");
        assertEquals("导入成功!", result.get("msg"), "消息应包含成功提示");

        // 验证：importUtil.importExcel 被调用一次
        verify(importUtil, times(1)).importExcel(any(), eq(DwsUsers.class), eq(TEST_SQL), isNull(), anyMap());
    }

    /**
     * 测试场景 2：导入成功但有重复数据被跳过
     * 期望：返回 AjaxResult，code=200，msg 包含跳过提示
     */
    @Test
    void testImportExcel_SuccessWithSkippedData() throws Exception {
        // 准备：模拟导入成功但跳过了 5 条重复数据
        ImportResultVo skippedResult = ImportResultVo.of(95, 5, Collections.emptyList());
        when(importUtil.importExcel(any(), eq(DwsUsers.class), eq(TEST_SQL), isNull(), anyMap()))
                .thenReturn(skippedResult);

        // 执行
        MockMultipartFile file = createValidExcelFile();

        AjaxResult result = userDataController.importExcel(file, TEST_ACCOUNT_ID);

        // 验证：返回 AjaxResult 成功响应
        assertNotNull(result, "返回结果不应为空");
        assertEquals(200, result.get("code"), "状态码应为 200");
        assertTrue(result.get("msg").toString().contains("跳过了"), "消息应包含跳过提示");
        assertTrue(result.get("msg").toString().contains("5"), "消息应包含跳过数量");
    }

    /**
     * 测试场景 3：导入失败（数据校验错误）
     * 期望：返回 AjaxResult 错误响应，code=500，msg=错误总述，data=错误详情列表
     * <p>
     * 这是本次改动测试用例。
     * <p>
     * 改动前：返回 Excel 文件（Content-Type: Excel，Content-Disposition: attachment）
     * 改动后：返回 AJAX JSON 错误响应（Content-Type: JSON，data 字段包含逐行错误详情）
     */
    @Test
    void testImportExcel_ImportException_ReturnsJsonError() throws Exception {
        // 准备：模拟导入失败，抛出 ImportException（数据校验错误）
        List<RowErrorVo> errors = new ArrayList<>();
        errors.add(new RowErrorVo<>(2L, "ref_date 格式错误", null));
        errors.add(new RowErrorVo<>(3L, "new_user 不能为空", null));
        ImportException importException = new ImportException("导入存在 2 条错误数据", errors);

        when(importUtil.importExcel(any(), eq(DwsUsers.class), eq(TEST_SQL), isNull(), anyMap()))
                .thenThrow(importException);

        // 执行：上传包含错误数据的文件
        MockMultipartFile file = createInvalidExcelFile();

        AjaxResult result = userDataController.importExcel(file, TEST_ACCOUNT_ID);

        // 验证：返回 AjaxResult 错误响应
        assertNotNull(result, "返回结果不应为空");
        assertEquals(500, result.get("code"), "状态码应为 500");
        assertEquals("导入存在 2 条错误数据", result.get("msg"), "消息应包含错误总述");

        // 验证：data 字段包含错误详情列表
        Object data = result.get("data");
        assertNotNull(data, "data 字段不应为空");
        assertTrue(data instanceof List, "data 应为 List 类型");
        @SuppressWarnings("unchecked")
        List<RowErrorVo> errorList = (List<RowErrorVo>) data;
        assertEquals(2, errorList.size(), "错误详情应包含 2 条记录");

        // 验证第一条错误
        RowErrorVo firstError = errorList.get(0);
        assertEquals(2L, firstError.getRowNo(), "第一条错误行号应为 2");
        assertEquals("ref_date 格式错误", firstError.getErrorMessage(), "第一条错误描述应为 ref_date 格式错误");

        // 验证第二条错误
        RowErrorVo secondError = errorList.get(1);
        assertEquals(3L, secondError.getRowNo(), "第二条错误行号应为 3");
        assertEquals("new_user 不能为空", secondError.getErrorMessage(), "第二条错误描述应为 new_user 不能为空");

        // 验证：importUtil.exportErrorList 不应被调用（不再导出生成 Excel 文件）
        verify(importUtil, never()).exportErrorList(any(), anyList(), anyMap());
    }

    /**
     * 测试场景 4：导入失败（运行时异常）
     * 期望：异常向上抛出，由全局异常处理器处理
     */
    @Test
    void testImportExcel_RuntimeException() throws Exception {
        // 准备：模拟运行时异常（如文件格式错误）
        when(importUtil.importExcel(any(), eq(DwsUsers.class), eq(TEST_SQL), isNull(), anyMap()))
                .thenThrow(new RuntimeException("文件格式不支持"));

        // 执行：上传无效文件
        MockMultipartFile file = createInvalidFormatFile();

        // 期望：抛出 RuntimeException
        assertThrows(RuntimeException.class, () -> {
            userDataController.importExcel(file, TEST_ACCOUNT_ID);
        });
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建有效的 Excel 测试文件
     */
    private MockMultipartFile createValidExcelFile() {
        return new MockMultipartFile(
                "file",
                "用户数据模板.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createMockExcelData()
        );
    }

    /**
     * 创建包含错误数据的 Excel 测试文件
     */
    private MockMultipartFile createInvalidExcelFile() {
        return new MockMultipartFile(
                "file",
                "用户数据模板-错误测试.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createMockExcelData()
        );
    }

    /**
     * 创建无效格式的文件
     */
    private MockMultipartFile createInvalidFormatFile() {
        return new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "这不是 Excel 文件".getBytes()
        );
    }

    /**
     * 创建模拟的 Excel 文件字节数据
     */
    private byte[] createMockExcelData() {
        // 返回一个简单的字节数组作为模拟 Excel 数据
        return new byte[]{0x50, 0x4B, 0x03, 0x04}; // ZIP/XLSX 文件头魔数
    }
}
