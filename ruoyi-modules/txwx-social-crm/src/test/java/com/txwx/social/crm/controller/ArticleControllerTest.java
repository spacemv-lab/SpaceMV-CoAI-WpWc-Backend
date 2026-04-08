package com.txwx.social.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.crm.domain.vo.ArticleVO;
import com.txwx.social.crm.service.IArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ArticleController 测试类
 *
 * @author txwx
 */
@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IArticleService articleService;

    private ArticleVO articleVO;

    @BeforeEach
    void setUp() throws Exception {
        articleVO = new ArticleVO();
        articleVO.setTitle("测试文章");
        articleVO.setContent("测试内容");
    }

    @Test
    void testAddDraft_Success() throws Exception {
        // 准备测试数据
        doNothing().when(articleService).addDraft(any(ArticleVO.class));

        // 执行测试
        mockMvc.perform(post("/article/addDraft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetDraftList_Success() throws Exception {
        // 执行测试
        mockMvc.perform(get("/article/draftList")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDraftCount_Success() throws Exception {
        // 执行测试
        mockMvc.perform(get("/article/draftCount"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDraftDetail_Success() throws Exception {
        // 执行测试
        mockMvc.perform(get("/article/draftDetail/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateDraft_Success() throws Exception {
        // 准备测试数据
        doNothing().when(articleService).updateDraft(anyLong(), any(ArticleVO.class));

        // 执行测试
        mockMvc.perform(post("/article/updateDraft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testDeleteDraft_Success() throws Exception {
        // 准备测试数据
        doNothing().when(articleService).deleteDraft(anyLong(),anyLong());

        // 执行测试
        mockMvc.perform(delete("/article/deleteDraft/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
