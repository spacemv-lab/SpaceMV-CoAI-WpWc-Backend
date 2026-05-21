package com.ruoyi.iam.service;

import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.iam.dto.LogoutQuery;
import com.ruoyi.iam.dto.vo.LogoutDetailVO;

/**
 * 注销管理 Service 接口（管理员）
 * <p>
 * 底层数据从 IAM 表派生，前端协议字段名保持不变。
 * 注销状态：0=冷却中, 1=已注销（已忽略「已撤销」状态）
 */
public interface IAdminLogoutService {

    /**
     * 分页查询注销申请列表
     *
     * @param query 查询参数
     * @return 分页结果
     */
    TableDataInfo queryLogoutList(LogoutQuery query);

    /**
     * 查询注销申请详情
     *
     * @param logoutId 注销ID（即用户ID）
     * @return 详情 VO
     */
    LogoutDetailVO queryLogoutDetail(Long logoutId);
}
