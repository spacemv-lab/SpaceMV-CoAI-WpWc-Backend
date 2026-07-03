package com.txwx.social.crm.mapper.content;

import com.txwx.social.crm.domain.content.ContentPublishResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContentPublishResultMapper {

    int insert(ContentPublishResult result);

    ContentPublishResult selectById(@Param("id") Long id);

    List<ContentPublishResult> selectByArticleId(@Param("articleId") Long articleId);

    List<ContentPublishResult> selectByJobId(@Param("jobId") Long jobId);

    List<ContentPublishResult> selectList(ContentPublishResult query);

    int update(ContentPublishResult result);
}
