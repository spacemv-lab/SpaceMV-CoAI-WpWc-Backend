package com.txwx.sync.center.service.impl;

import com.txwx.social.api.domain.dto.WriteResultDTO;
import com.txwx.social.api.spi.ISyncServiceProvider;
import com.txwx.social.api.spi.SyncServiceProviderFactory;
import com.txwx.sync.center.domain.po.SyncLogPO;
import com.txwx.sync.center.domain.po.SyncTaskPO;
import com.txwx.sync.center.mapper.SyncLogMapper;
import com.txwx.sync.center.mapper.SyncTaskMapper;
import com.txwx.sync.center.service.ISyncLogService;
import com.txwx.sync.center.service.ISyncTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 同步任务服务实现类
 *
 * @author txwx
 * @date 2026-04-03
 */
@Service
public class SyncTaskServiceImpl implements ISyncTaskService {

    private static final Logger logger = LoggerFactory.getLogger(SyncTaskServiceImpl.class);

    @Autowired
    private SyncTaskMapper syncTaskMapper;

    @Autowired
    private SyncLogMapper syncLogMapper;

    @Autowired
    private ISyncLogService syncLogService;

    @Autowired
    private SyncServiceProviderFactory syncServiceProviderFactory;

    @Override
    public List<SyncTaskPO> selectTaskList(SyncTaskPO task) {
        return syncTaskMapper.selectTaskList(task);
    }

    @Override
    public SyncTaskPO selectTaskById(Long id) {
        return syncTaskMapper.selectTaskById(id);
    }

    @Override
    public int insertTask(SyncTaskPO task) {
        return syncTaskMapper.insertTask(task);
    }

    @Override
    public int updateTask(SyncTaskPO task) {
        return syncTaskMapper.updateTask(task);
    }

    @Override
    public int deleteTaskByIds(Long[] ids) {
        return syncTaskMapper.deleteTaskByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String executeTask(Long taskId) {
        SyncTaskPO task = syncTaskMapper.selectTaskById(taskId);
        if (task == null) {
            return "任务不存在";
        }

        // 更新任务状态为执行中
        task.setTaskStatus("1");
        syncTaskMapper.updateTask(task);

        String result = "";
        try {
            // 获取 SPI 提供者
            ISyncServiceProvider provider = syncServiceProviderFactory.getProvider(task.getChannelType());
            if (provider == null) {
                throw new Exception("未找到渠道类型为 " + task.getChannelType() + " 的同步提供者");
            }

            String startDateStr = task.getStartDate();
            String endDateStr = task.getEndDate();

            // 第一阶段：获取数据
            task.setTaskStatus("11"); // 数据抓取中
            syncTaskMapper.updateTask(task);

            Map<String, List<?>> fetchDataResult = provider.fetchData(
                    LocalDate.parse(startDateStr),
                    LocalDate.parse(endDateStr),
                    task.getAccountId()
            );

            // 更新任务状态为数据抓取完成
            task.setTaskStatus("12"); // 数据写入中
            syncTaskMapper.updateTask(task);

            // 第二阶段：写入数据（分数据类型写入）
            if (fetchDataResult != null && !fetchDataResult.isEmpty()) {
                WriteResultDTO writeResult = provider.writeData(fetchDataResult, task.getAccountId());

                // 更新任务状态为已完成
                task.setTaskStatus("2");
                task.setSyncCount(writeResult.getCount());
                syncTaskMapper.updateTask(task);

                // 记录同步日志
                SyncLogPO log = new SyncLogPO();
                log.setTaskId(taskId);
                log.setChannelType(task.getChannelType());
                log.setAccountId(task.getAccountId());
                log.setDataCount(writeResult.getCount());
                log.setStatus(writeResult.isSuccess() ? "0" : "1");
                log.setErrorMessage(writeResult.getErrorMessage());
                syncLogMapper.insertLog(log);

                result = "同步成功，写入条数: " + writeResult.getCount();
            } else {
                // 无数据
                task.setTaskStatus("2");
                task.setSyncCount(0);
                syncTaskMapper.updateTask(task);

                SyncLogPO log = new SyncLogPO();
                log.setTaskId(taskId);
                log.setChannelType(task.getChannelType());
                log.setAccountId(task.getAccountId());
                log.setDataCount(0);
                log.setStatus("0");
                syncLogMapper.insertLog(log);

                result = "同步成功，无数据";
            }
        } catch (Exception e) {
            logger.error("执行同步任务失败: taskId=" + taskId, e);
            task.setTaskStatus("3");
            task.setErrorMessage(e.getMessage());
            syncTaskMapper.updateTask(task);

            // 记录同步日志
            SyncLogPO log = new SyncLogPO();
            log.setTaskId(taskId);
            log.setChannelType(task.getChannelType());
            log.setAccountId(task.getAccountId());
            log.setStatus("1");
            log.setErrorMessage(e.getMessage());
            syncLogMapper.insertLog(log);

            result = "同步失败: " + e.getMessage();
        }

        return result;
    }

    @Override
    public List<SyncTaskPO> selectPendingTasks() {
        SyncTaskPO task = new SyncTaskPO();
        task.setTaskStatus("0");
        return syncTaskMapper.selectTaskList(task);
    }
}
