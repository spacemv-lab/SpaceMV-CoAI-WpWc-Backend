package com.txwx.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.txwx.web.domain.TxwxCompanyInfoTemp;
import com.txwx.web.domain.TxwxCompanyProfileTemp;
import com.txwx.web.domain.TxwxCompanyTemp;
import com.txwx.web.service.ICompanyDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CompanyDetailController 测试类
 *
 * @author txwx
 */
@WebMvcTest(CompanyDetailController.class)
public class CompanyDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICompanyDetailService companyDetailService;

    private TxwxCompanyTemp companyTemp;

    @BeforeEach
    void setUp() {
        companyTemp = new TxwxCompanyTemp();

        TxwxCompanyInfoTemp txwxCompanyInfoTemp = new TxwxCompanyInfoTemp();
        txwxCompanyInfoTemp.setId(1L);
        txwxCompanyInfoTemp.setCompanyName("测试公司");

        companyTemp.setTxwxCompanyInfoTemp(txwxCompanyInfoTemp);
    }

    @Test
    void testDisplayConfig_Success() throws Exception {
        // 执行测试
        mockMvc.perform(get("/company/displayConfig"))
                .andExpect(status().isOk());
    }

    @Test
    void testPreviewConfig_Success() throws Exception {
        // 执行测试
        mockMvc.perform(get("/company/previewConfig"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveCompanyTemp_Success() throws Exception {
        // 准备测试数据
        when(companyDetailService.saveCompanyTemp(any(TxwxCompanyTemp.class))).thenReturn(1);

        // 执行测试
        mockMvc.perform(post("/company/saveCompanyTemp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyTemp)))
                .andExpect(status().isOk());

        // 验证调用
        verify(companyDetailService, times(1)).saveCompanyTemp(any(TxwxCompanyTemp.class));
    }

    @Test
    void testPublishCompany_Success() throws Exception {
        // 准备测试数据
        when(companyDetailService.publishCompany()).thenReturn(1);

        // 执行测试
        mockMvc.perform(post("/company/publishCompany"))
                .andExpect(status().isOk());

        // 验证调用
        verify(companyDetailService, times(1)).publishCompany();
    }
}
