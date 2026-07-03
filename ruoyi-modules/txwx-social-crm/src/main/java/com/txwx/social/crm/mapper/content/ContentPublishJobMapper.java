package com.txwx.social.crm.mapper.content;

import com.txwx.social.crm.domain.content.ContentPublishJob;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContentPublishJobMapper {

    int insert(ContentPublishJob job);

    int update(ContentPublishJob job);

    ContentPublishJob selectById(@Param("id") Long id);

    List<ContentPublishJob> selectByArticleId(@Param("articleId") Long articleId);

    List<ContentPublishJob> selectList(ContentPublishJob query);

    ContentPublishJob selectSuccessByArticleIdAndTarget(@Param("articleId") Long articleId, @Param("targetCode") String targetCode);

    List<ContentPublishJob> selectProcessingByTargetCode(@Param("targetCode") String targetCode);
}
