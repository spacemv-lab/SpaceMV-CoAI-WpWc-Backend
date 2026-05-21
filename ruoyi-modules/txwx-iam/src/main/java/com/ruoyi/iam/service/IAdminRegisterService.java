package com.ruoyi.iam.service;

import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.iam.dto.RegisterQuery;
import com.ruoyi.iam.dto.UpdateStatusRequest;
import com.ruoyi.iam.dto.vo.RegisterDetailVO;
import com.ruoyi.iam.dto.vo.RegisterListVO;

import java.util.List;

/**
 * 注册用户管理 Service 接口（管理员）
 * <p>
 * 前端 API 协议与 CRM 保持一致，底层数据从 IAM 表派生
 */
public interface IAdminRegisterService {

    /**
     * 分页查询注册用户列表
     *
     * @param query 查询参数
     * @return 分页结果（VO 已映射好字段名）
     */
    TableDataInfo queryRegisterList(RegisterQuery query);

    /**
     * 查询注册用户详情
     *
     * @param registerId 注册ID（即用户ID）
     * @return 详情 VO
     */
    RegisterDetailVO queryRegisterDetail(Long registerId);

    /**
     * 修改注册用户状态（启用/停用）
     */
    AjaxResult updateStatus(UpdateStatusRequest request);
}
