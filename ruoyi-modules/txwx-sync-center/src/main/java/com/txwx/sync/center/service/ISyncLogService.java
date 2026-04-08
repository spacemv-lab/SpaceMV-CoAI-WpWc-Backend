package com.txwx.sync.center.service;

import com.txwx.sync.center.domain.po.SyncLogPO;

import java.util.List;

/**
 * 同步日志服务接口
 *
 * @author txwx
 * @date 2026-04-03
 */
public interface ISyncLogService {

    /**
     * 查询日志列表
     *
     * @param log 日志参数
     * @return 日志列表
     */
    List<SyncLogPO> selectLogList(SyncLogPO log);

    /**
     * 查询日志详情
     *
     * @param id 主键ID
     * @return 日志详情
     */
    SyncLogPO selectLogById(Long id);

    /**
     * 新增日志
     *
     * @param log 日志信息
     * @return 结果
     */
    int insertLog(SyncLogPO log);

    /**
     * 查询最新的同步状态
     *
     * @param accountId 账号ID
     * @return 最新同步状态
     */
    SyncLogPO selectLatestSyncStatus(Long accountId);
}
