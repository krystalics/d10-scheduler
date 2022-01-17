package com.github.krystalics.d10.scheduler.resource.manager.fifo;

import com.github.krystalics.d10.scheduler.resource.manager.ResourceScheduler;
import com.github.krystalics.d10.scheduler.resource.manager.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @Author linjiabao001
 * @Date 2022/1/17 11:46
 * @Description FIFO策略
 */
@Component
public class FIFOScheduler implements ResourceScheduler {

    @Autowired
    private ResourceService resourceService;

    @Override
    public String resourceAllocator(long instanceId, String queueName, double cpuApply, double memoryApply) {
        return resourceAllocator(instanceId, queueName, cpuApply, memoryApply, true);
    }


    /**
     * 需要对 queueName进行分布式锁，或者
     * @param instanceId  申请资源的任务
     * @param queueName   预设想要申请资源的队列
     * @param cpuApply    需要的cpu
     * @param memoryApply 需要的内存
     * @param scramble    true开启争抢模式，false则不争抢
     * @return
     */
    @Override
    public String resourceAllocator(long instanceId, String queueName, double cpuApply, double memoryApply, boolean scramble) {
        //todo lock获取queue锁、悲观锁并发粒度小
        resourceService.resourceAndInstanceStateUpdate();
        return "";
    }


}
