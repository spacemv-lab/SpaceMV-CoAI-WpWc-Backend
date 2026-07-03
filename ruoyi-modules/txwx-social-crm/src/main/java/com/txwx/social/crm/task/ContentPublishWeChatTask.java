package com.txwx.social.crm.task;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.exception.ServiceException;
import com.txwx.social.crm.domain.content.ContentPublishJob;
import com.txwx.social.crm.domain.content.ContentPublishResult;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.dto.GetPublishStatusResponse;
import com.txwx.social.crm.mapper.content.ContentPublishJobMapper;
import com.txwx.social.crm.mapper.content.ContentPublishResultMapper;
import com.txwx.social.crm.publisher.TargetCodes;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.util.WebChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentPublishWeChatTask {

    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";

    private final ContentPublishJobMapper publishJobMapper;
    private final ContentPublishResultMapper publishResultMapper;
    private final IAccountService accountService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void syncWeChatPublishStatus() {
        log.debug("开始执行定时任务：同步微信公众号发布状态");
        try {
            List<ContentPublishJob> processingJobs = publishJobMapper.selectProcessingByTargetCode(
                    TargetCodes.WECHAT_OFFICIAL_ACCOUNT);
            if (processingJobs == null || processingJobs.isEmpty()) {
                return;
            }

            log.info("需要同步发布状态的微信任务数量: {}", processingJobs.size());
            for (ContentPublishJob job : processingJobs) {
                try {
                    processJob(job);
                } catch (Exception e) {
                    log.error("同步微信发布状态失败, jobId={}: {}", job.getId(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("同步微信发布状态定时任务执行失败", e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void processJob(ContentPublishJob job) {
        ContentPublishResult result = publishResultMapper.selectByJobId(job.getId()).stream()
                .findFirst().orElse(null);
        if (result == null) {
            log.warn("发布任务 {} 没有对应的发布结果记录", job.getId());
            return;
        }

        String resultJson = result.getResultJson();
        if (resultJson == null || resultJson.isBlank()) {
            return;
        }

        JSONObject resultObj = JSONObject.parseObject(resultJson);
        String publishId = resultObj.getString("publishId");
        if (publishId == null || publishId.isBlank()) {
            return;
        }

        String payloadJson = result.getPayloadJson();
        Long accountId = null;
        if (payloadJson != null && !payloadJson.isBlank()) {
            JSONObject payloadObj = JSONObject.parseObject(payloadJson);
            accountId = payloadObj.getLong("accountId");
        }
        if (accountId == null) {
            log.warn("发布任务 {} 未找到accountId, 跳过", job.getId());
            return;
        }

        TxwxAccountPO account = accountService.selectAccountById(accountId);
        if (account == null) {
            log.warn("微信公众号账号不存在, accountId={}", accountId);
            return;
        }

        String accessToken;
        try {
            accessToken = WebChatUtil.getAccessToken(account.getAppId(), account.getSecret());
        } catch (Exception e) {
            log.error("获取微信公众号access_token失败, accountId={}: {}", accountId, e.getMessage());
            return;
        }

        GetPublishStatusResponse statusResponse;
        try {
            statusResponse = WebChatUtil.getPublishStatus(accessToken, publishId);
        } catch (Exception e) {
            log.error("查询微信发布状态失败, publishId={}: {}", publishId, e.getMessage());
            return;
        }

        Integer publishStatus = statusResponse.getPublish_status();
        if (publishStatus == null) {
            return;
        }

        if (publishStatus == 1) {
            log.debug("微信发布任务 {} 仍在发布中, publishId={}", job.getId(), publishId);
            return;
        }

        ContentPublishJob updateJob = new ContentPublishJob();
        updateJob.setId(job.getId());

        if (publishStatus == 0) {
            updateJob.setStatus(STATUS_SUCCESS);
            updateJob.setFinishedAt(new Date());

            String articleUrl = null;
            if (statusResponse.getArticle_detail() != null
                    && statusResponse.getArticle_detail().getItem() != null
                    && !statusResponse.getArticle_detail().getItem().isEmpty()) {
                articleUrl = statusResponse.getArticle_detail().getItem().get(0).getArticle_url();
            }
            if (articleUrl == null && statusResponse.getArticle_id() != null) {
                articleUrl = "https://mp.weixin.qq.com/s/" + statusResponse.getArticle_id();
            }
            updateJob.setExternalUrl(articleUrl);

            publishJobMapper.update(updateJob);

            ContentPublishResult updateResult = new ContentPublishResult();
            updateResult.setId(result.getId());
            updateResult.setExternalUrl(articleUrl);
            updateResult.setResultJson("{\"status\":\"SUCCESS\",\"publishId\":\"" + publishId
                    + "\",\"articleUrl\":\"" + (articleUrl != null ? articleUrl : "") + "\"}");
            publishResultMapper.update(updateResult);

            log.info("微信发布任务 {} 成功, articleUrl={}", job.getId(), articleUrl);
        } else {
            updateJob.setStatus(STATUS_FAILED);
            updateJob.setFinishedAt(new Date());
            updateJob.setErrorMessage("微信发布失败, publish_status=" + publishStatus);

            publishJobMapper.update(updateJob);

            ContentPublishResult updateResult = new ContentPublishResult();
            updateResult.setId(result.getId());
            updateResult.setResultJson("{\"status\":\"FAILED\",\"publishId\":\"" + publishId
                    + "\",\"publishStatus\":" + publishStatus + "}");
            publishResultMapper.update(updateResult);

            log.warn("微信发布任务 {} 失败, publishStatus={}", job.getId(), publishStatus);
        }
    }
}
