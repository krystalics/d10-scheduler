package com.github.krystalics.d10.scheduler.resource.manager.service;

import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
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
    private SchedulerMapper schedulerMapper;

    @Autowired
    private LockService lockService;

    @Transactional(rollbackFor = Throwable.class)
    public String resourceAndInstanceStateUpdate(long instanceId, String queueName, double cpuApply, double memoryApply, boolean scramble) throws Exception {
        //todo 1.check这个queueName使用的资源有没有达到min
        //todo 2.没有的话尝试更新；有的话，尝试开启争抢模式，去争抢同优先级队列或者之下的 资源
        //todo 3.获取资源后更新自身的状态


        if (scramble) {
            boolean tryLock = lockService.tryLock("other queue", 0);
            if (tryLock) {

            }

        }

        schedulerMapper.updateInstance(null);

        return "";
    }
}
