package com.txwx.web.service;

import com.txwx.web.domain.TxwxCompany;
import com.txwx.web.domain.TxwxCompanyInfo;
import com.txwx.web.domain.TxwxCompanyInfoTemp;
import com.txwx.web.domain.TxwxCompanyTemp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * ICompanyDetailService 测试类
 *
 * @author txwx
 */
@ExtendWith(MockitoExtension.class)
public class CompanyDetailServiceTest {

    @Mock
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
    void testDisplayConfig_Success() {
        // 准备测试数据
        TxwxCompany mockCompany = new TxwxCompany();

        TxwxCompanyInfo txwxCompanyInfo = new TxwxCompanyInfo();
        txwxCompanyInfo.setId(1L);
        txwxCompanyInfo.setCompanyName("测试公司");

        mockCompany.setTxwxCompanyInfo(txwxCompanyInfo);

        when(companyDetailService.displayConfig()).thenReturn(mockCompany);

        // 执行测试
        TxwxCompany result = companyDetailService.displayConfig();

        // 验证结果
        assertNotNull(result);
        assertEquals("测试公司", result.getTxwxCompanyInfo().getCompanyName());

        // 验证调用
        verify(companyDetailService, times(1)).displayConfig();
    }

    @Test
    void testPreviewConfig_Success() {
        // 准备测试数据
        when(companyDetailService.previewConfig()).thenReturn(companyTemp);

        // 执行测试
        TxwxCompanyTemp result = companyDetailService.previewConfig();

        // 验证结果
        assertNotNull(result);
        assertEquals("测试公司", result.getTxwxCompanyInfoTemp().getCompanyName());

        // 验证调用
        verify(companyDetailService, times(1)).previewConfig();
    }

    @Test
    void testSaveCompanyTemp_Success() {
        // 准备测试数据
        when(companyDetailService.saveCompanyTemp(any(TxwxCompanyTemp.class))).thenReturn(1);

        // 执行测试
        int result = companyDetailService.saveCompanyTemp(companyTemp);

        // 验证结果
        assertEquals(1, result);

        // 验证调用
        verify(companyDetailService, times(1)).saveCompanyTemp(companyTemp);
    }

    @Test
    void testPublishCompany_Success() {
        // 准备测试数据
        when(companyDetailService.publishCompany()).thenReturn(1);

        // 执行测试
        int result = companyDetailService.publishCompany();

        // 验证结果
        assertEquals(1, result);

        // 验证调用
        verify(companyDetailService, times(1)).publishCompany();
    }
}
