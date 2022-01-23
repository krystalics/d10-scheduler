package com.github.krystalics.d10.scheduler.core.transaction;

import com.github.krystalics.d10.scheduler.common.constant.VersionState;
import com.github.krystalics.d10.scheduler.dao.entity.Instance;
import com.github.krystalics.d10.scheduler.dao.mapper.InstanceMapper;
import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private InstanceMapper instanceMapper;

    /**
     * 防止更新了task的concurrency时宕机而instance状态未更新 导致并发度被空占
     *
     * @param taskId 需要更新的taskId
     * @param instanceId 需要更新的instanceId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void updateTaskAndInstance(long taskId,long instanceId) {
        int cnt = taskMapper.updateTaskConcurrentOccupation(taskId);
        //结果为0说明没有更新成功，没有占到资源;
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
