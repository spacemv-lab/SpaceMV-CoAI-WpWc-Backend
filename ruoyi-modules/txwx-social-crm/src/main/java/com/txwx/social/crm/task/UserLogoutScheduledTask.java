package com.txwx.social.crm.task;

import com.ruoyi.common.core.utils.StringUtils;
import com.txwx.social.crm.domain.po.TxwxUserLogoutPO;
import com.txwx.social.crm.domain.po.TxwxUserRegisterPO;
import com.txwx.social.crm.service.ITxwxUserLogoutService;
import com.txwx.social.crm.service.ITxwxUserRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 账号注销定时任务（每日凌晨2点执行）
 *
 * @author txwx
 * @date 2026-04-20
 */
@Component
@Slf4j
public class UserLogoutScheduledTask {

    @Autowired
    private ITxwxUserLogoutService userLogoutService;

    @Autowired
    private ITxwxUserRegisterService userRegisterService;

    /**
     * 每日凌晨2点执行，自动处理冷却期结束的注销申请
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoExecuteLogout() {
        log.info("开始执行账号注销定时任务");

        // 1. 查询冷却期结束的注销申请
        List<TxwxUserLogoutPO> coolingCompleted = userLogoutService.selectCoolingCompleted();

        if (coolingCompleted.isEmpty()) {
            log.info("没有需要处理的注销申请");
            return;
        }

        // 2. 逐个处理
        for (TxwxUserLogoutPO logout : coolingCompleted) {
            try {
                // 更新注销申请状态为已注销
                logout.setStatus("1"); // 已注销
                logout.setUpdateBy("system");
                userLogoutService.updateById(logout);

                // 更新注册用户状态为已注销
                TxwxUserRegisterPO register = userRegisterService.selectByUserId(logout.getUserId());
                if (register != null) {
                    register.setStatus("3"); // 已注销
                    register.setUpdateBy("system");
                    userRegisterService.updateById(register);
                }

                log.info("账号注销完成: userId={}, logoutId={}", logout.getUserId(), logout.getLogoutId());
            } catch (Exception e) {
                log.error("账号注销失败: userId={}, logoutId={}, error={}", logout.getUserId(), logout.getLogoutId(), e.getMessage());
            }
        }

        log.info("账号注销定时任务执行完成，共处理{}条记录", coolingCompleted.size());
    }

    /**
     * 每小时清理一次过期的撤销/已注销记录（保留最近30天）
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void cleanupExpiredRecords() {
        log.info("开始清理过期记录");

        // TODO: 清理逻辑（可选）
        // 可以根据业务需求清理超过30天的历史记录
    }
}
