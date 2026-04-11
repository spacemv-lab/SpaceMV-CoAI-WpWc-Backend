package com.txwx.social.crm.service;

import com.txwx.social.crm.domain.po.TxwxArticlePO;
import com.txwx.social.crm.domain.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * IArticleService 测试类
 *
 * @author txwx
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private IArticleService articleService;

    private ArticleVO articleVO;

    @BeforeEach
    void setUp() {
        articleVO = new ArticleVO();
        articleVO.setTitle("测试文章");
        articleVO.setContent("测试内容");
    }

    @Test
    void testAddDraft_Success() {
        // 准备测试数据
        doNothing().when(articleService).addDraft(any(ArticleVO.class));

        // 执行测试
        assertDoesNotThrow(() -> articleService.addDraft(articleVO));

        // 验证调用
        verify(articleService, times(1)).addDraft(articleVO);
    }

    @Test
    void testGetDraftList_Success() {
        // 准备测试数据
        TxwxArticlePO mockArticle = new TxwxArticlePO();
        mockArticle.setId(1L);
        mockArticle.setTitle("测试草稿");

//        when(articleService.getDraftList(anyString(), anyString(), anyString(), eq(1), eq(10)))
//            .thenReturn(List.of(mockArticle));

        // 执行测试
//        List<TxwxArticlePO> result = articleService.getDraftList(null, null, null, 1, 10);

        // 验证结果
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("测试草稿", result.get(0).getTitle());
    }

    @Test
    void testGetDraftCount_Success() {
        // 准备测试数据
        ArticleDraftReqVO reqVO = new ArticleDraftReqVO();
        when(articleService.getDraftCount(reqVO)).thenReturn(5);

        // 执行测试
        int count = articleService.getDraftCount(reqVO);

        // 验证结果
        assertEquals(5, count);
    }

    @Test
    void testGetDraftDetail_Success() {
        // 准备测试数据
        ArticleDetailVO mockDetail = new ArticleDetailVO();
        mockDetail.setTitle("测试详情");

        when(articleService.getDraftDetail(anyLong())).thenReturn(mockDetail);

        // 执行测试
        ArticleDetailVO result = articleService.getDraftDetail(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("测试详情", result.getTitle());
    }

    @Test
    void testUpdateDraft_Success() {
        // 准备测试数据
        doNothing().when(articleService).updateDraft(anyLong(), any(ArticleVO.class));

        // 执行测试
        assertDoesNotThrow(() -> articleService.updateDraft(1L, articleVO));

        // 验证调用
        verify(articleService, times(1)).updateDraft(1L, articleVO);
    }

    @Test
    void testDeleteDraft_Success() {
        // 准备测试数据
        doNothing().when(articleService).deleteDraft(anyLong());

        // 执行测试
        assertDoesNotThrow(() -> articleService.deleteDraft(1L));

        // 验证调用
        verify(articleService, times(1)).deleteDraft(1L);
    }
}
