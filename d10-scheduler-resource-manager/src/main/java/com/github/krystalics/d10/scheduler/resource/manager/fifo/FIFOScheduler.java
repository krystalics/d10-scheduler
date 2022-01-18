package com.github.krystalics.d10.scheduler.resource.manager.fifo;

import com.github.krystalics.d10.scheduler.common.constant.VersionState;
import com.github.krystalics.d10.scheduler.dao.entity.Instance;
import com.github.krystalics.d10.scheduler.dao.entity.Queue;
import com.github.krystalics.d10.scheduler.dao.mapper.QueueMapper;
import com.github.krystalics.d10.scheduler.resource.manager.ResourceScheduler;
import com.github.krystalics.d10.scheduler.resource.manager.common.ResourceConstants;
import com.github.krystalics.d10.scheduler.resource.manager.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
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
    private CuratorFramework client;

    @Autowired
    private QueueMapper queueMapper;

    @Override
    public String resourceAllocator(long instanceId, String queueName, double cpuApply, double memoryApply) throws Exception {
        return resourceAllocator(instanceId, queueName, cpuApply, memoryApply, true);
    }


    /**
     * 1./lock-queue/{queueName} 进行分布式锁
     * 2.对队列资源进行判断、因为cpu属于可压缩资源，暂时只考虑内存的资源情况
     * - 1.inUse+apply >= max 申请失败
     * - 2.inUse+apply <= min 申请成功
     * - 3.min < inUse+apply < max 开启争抢
     *
     * @param instanceId  申请资源的任务
     * @param queueName   预设想要申请资源的队列
     * @param cpuApply    需要的cpu
     * @param memoryApply 需要的内存
     * @param scramble    true开启争抢模式，false则不争抢
     * @return
     */
    @Override
    public String resourceAllocator(long instanceId, String queueName, double cpuApply, double memoryApply, boolean scramble) throws Exception {
        InterProcessSemaphoreMutex mutex = new InterProcessSemaphoreMutex(client, ResourceConstants.LOCK_PREFIX + queueName);

        try {
            mutex.acquire();

            final Queue queue = queueMapper.getByName(queueName);
            final double memWillUse = queue.getMemoryInUse() + memoryApply;
            if (memWillUse >= queue.getMemoryMax()) {
                return "";
            }

            if (memWillUse <= queue.getMemoryMin()) {
                Instance instance = new Instance();
                instance.setInstanceId(instanceId);
                instance.setQueueName(queueName);
                instance.setState(VersionState.PENDING.getState());

                queue.setMemoryInUse(memWillUse);
                queue.setCpuInUse(queue.getCpuInUse() + cpuApply);
                //事务
                resourceService.resourceAndInstanceStateUpdate(instance, queue);
                return queueName;
            }

            if (memWillUse > queue.getMemoryMin() && memWillUse < queue.getMemoryMax()) {
                //非争抢的直接返回
                if (!scramble) {
                    return "";
                }
                //todo 尝试获取其他队列的锁

            }

        } catch (Exception e) {
            log.error("resource allocator exception ", e);
        } finally {
            mutex.release();
        }

        return "";
    }


}
