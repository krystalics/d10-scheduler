package com.github.krystalics.d10.scheduler.resource.manager.fifo;

import com.github.krystalics.d10.scheduler.resource.manager.ResourceScheduler;
import com.github.krystalics.d10.scheduler.resource.manager.common.ResourceConstants;
import com.github.krystalics.d10.scheduler.common.zk.LockService;
import com.github.krystalics.d10.scheduler.resource.manager.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author linjiabao001
 * @Date 2022/1/17 11:46
 * @Description FIFO策略
 */
@Component
@Slf4j
public class FIFOScheduler implements ResourceScheduler {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private LockService lockService;

    @Override
    public String resourceAllocator(long instanceId, String queueName, double cpuApply, double memoryApply) throws Exception {
        return resourceAllocator(instanceId, queueName, cpuApply, memoryApply, true);
    }


    /**
     * 1./lock-queue/{queueName} 进行分布式锁
     * 2.
     *
     * @param instanceId  申请资源的任务
     * @param queueName   预设想要申请资源的队列
     * @param cpuApply    需要的cpu
     * @param memoryApply 需要的内存
     * @param scramble    true开启争抢模式，false则不争抢
     * @return
     */
    @Override
    public synchronized String resourceAllocator(long instanceId, String queueName, double cpuApply, double memoryApply, boolean scramble) throws Exception {
        try {
            lockService.lock(ResourceConstants.LOCK_PREFIX + queueName);
            resourceService.resourceAndInstanceStateUpdate(instanceId, queueName, cpuApply, memoryApply, scramble);
        } catch (Exception e) {
            log.error("resource allocator exception ", e);
        } finally {
            lockService.unlock(ResourceConstants.LOCK_PREFIX + queueName);
        }

        return "";
    }


}
