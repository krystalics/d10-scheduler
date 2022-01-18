package com.github.krystalics.d10.scheduler.resource.manager.service;

import com.github.krystalics.d10.scheduler.dao.entity.Instance;
import com.github.krystalics.d10.scheduler.dao.entity.Queue;
import com.github.krystalics.d10.scheduler.dao.mapper.InstanceMapper;
import com.github.krystalics.d10.scheduler.dao.mapper.QueueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author linjiabao001
 * @Date 2022/1/17
 * @Description
 */
@Service
public class ResourceService {

    @Autowired
    private InstanceMapper instanceMapper;

    @Autowired
    private QueueMapper queueMapper;


    /**
     * instance -> queueName,state
     * queue    -> cpuInUse,memoryInUse
     */
    @Transactional(rollbackFor = Throwable.class)
    public void resourceAndInstanceStateUpdate(Instance instance, Queue queue) {
        instanceMapper.update(instance);
        queueMapper.update(queue);
    }
}
