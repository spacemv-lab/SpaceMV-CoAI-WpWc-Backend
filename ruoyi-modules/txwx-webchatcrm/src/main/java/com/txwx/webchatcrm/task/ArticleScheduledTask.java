/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 
 * the copywrite is belongs to  SpaceMV  team
 */
package com.txwx.webchatcrm.task;

import com.txwx.webchatcrm.service.IArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
            articleService.updatePublishingArticleStatus();
            logger.info("定时任务执行完成：更新发布中文章的状态");
        } catch (Exception e) {
            logger.error("定时任务执行失败：更新发布中文章的状态", e);
        }
    }
}
