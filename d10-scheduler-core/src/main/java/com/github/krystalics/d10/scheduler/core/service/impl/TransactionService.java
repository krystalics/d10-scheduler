package com.github.krystalics.d10.scheduler.core.service.impl;

import com.github.krystalics.d10.scheduler.common.constant.VersionState;
import com.github.krystalics.d10.scheduler.dao.entity.Instance;
import com.github.krystalics.d10.scheduler.dao.entity.Task;
import com.github.krystalics.d10.scheduler.dao.entity.Version;
import com.github.krystalics.d10.scheduler.dao.mapper.InstanceMapper;
import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import com.github.krystalics.d10.scheduler.dao.mapper.VersionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author linjiabao001
 * @date 2022/1/23
 * @description
 */
@Service
@Slf4j
public class TransactionService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private VersionMapper versionMapper;

    @Autowired
    private InstanceMapper instanceMapper;

    /**
     * 防止更新了task的concurrency时宕机而instance状态未更新 导致并发度被空占
     *
     * @param taskId     需要更新的taskId
     * @param instanceId 需要更新的instanceId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void updateTaskAndInstance(long taskId, long instanceId) {
        int cnt = taskMapper.updateTaskConcurrentOccupation(taskId);
        //结果为0说明没有更新成功，没有占到资源;直接返回即可
        if (cnt == 0) {
            log.error("task {} concurrent occupation is fulled! please increase the concurrency", taskId);
            return;
        }
        Instance instance = new Instance();
        instance.setInstanceId(instanceId);
        instance.setState(VersionState.WAITING_RES.getState());
        instanceMapper.update(instance);
    }
}
