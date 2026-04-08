package com.txwx.sync.center.mapper;

import com.txwx.sync.center.domain.po.SyncTaskPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 同步任务Mapper
 *
 * @author txwx
 * @date 2026-04-03
 */
@Mapper
public interface SyncTaskMapper {

    /**
     * 查询任务列表
     *
     * @param task 任务参数
     * @return 任务列表
     */
    List<SyncTaskPO> selectTaskList(SyncTaskPO task);

    /**
     * 查询任务详情
     *
     * @param id 主键ID
     * @return 任务详情
     */
    SyncTaskPO selectTaskById(Long id);

    /**
     * 新增任务
     *
     * @param task 任务信息
     * @return 结果
     */
    int insertTask(SyncTaskPO task);

    /**
     * 修改任务
     *
     * @param task 任务信息
     * @return 结果
     */
    int updateTask(SyncTaskPO task);

    /**
     * 删除任务
     *
     * @param ids 主键ID列表
     * @return 结果
     */
    int deleteTaskByIds(Long[] ids);

    /**
     * 查询待执行的任务
     *
     * @return 待执行任务列表
     */
    List<SyncTaskPO> selectPendingTasks();
}
