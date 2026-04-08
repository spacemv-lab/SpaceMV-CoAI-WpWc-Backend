package com.txwx.social.crm.task;

import com.txwx.social.crm.service.IArticleService;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 微信公众号文章定时任务
 *
 * @author txwx
 * @date 2025-01-21
 */
@Component
public class ArticleScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ArticleScheduledTask.class);

    @Autowired
    private IArticleService articleService;

    /**
     * @description: 定时任务：每分钟更新发布中文章的状态
     *               查询状态为"发布中"的文章，调用微信API获取最新发布状态并更新数据库
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void updatePublishingArticleStatus() {
        try {
            logger.info("开始执行定时任务：更新发布中文章的状态");
            List<Long> accountIds = Lists.newArrayList();
            accountIds.add(1L);
            articleService.updatePublishingArticleStatus(accountIds);
            logger.info("定时任务执行完成：更新发布中文章的状态");
        } catch (Exception e) {
            logger.error("定时任务执行失败：更新发布中文章的状态", e);
        }
    }
}
