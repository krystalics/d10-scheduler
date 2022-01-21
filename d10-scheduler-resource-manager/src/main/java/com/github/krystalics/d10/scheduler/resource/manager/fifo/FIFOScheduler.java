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

import java.util.List;
import java.util.concurrent.TimeUnit;

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
     * 基于资源争抢会比较频繁，采用悲观锁的形式。而不是用update去尝试着更新，这种乐观锁的形式
     *
     * 1./lock-queue/{queueName} 进行分布式锁
     * 2.对队列资源进行判断、因为cpu属于可压缩资源，暂时只考虑内存的资源情况
     * - 1.memWillUse >= queue.getMemoryMax() || cpuWillUse >= queue.getCpuMax()  申请失败
     * - 2.memWillUse <= queue.getMemoryMin() && cpuWillUse <= queue.getCpuMin()  申请成功
     * - 3.(memWillUse > queue.getMemoryMin() && memWillUse < queue.getMemoryMax())
     * ||(cpuWillUse > queue.getCpuMin() && cpuWillUse < queue.getCpuMax())   两个资源中的一个大于min了就开启争抢
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
        InterProcessSemaphoreMutex mutex = new InterProcessSemaphoreMutex(client, ResourceConstants.LOCK_QUEUE_PREFIX + queueName);

        try {
            mutex.acquire();

            final Queue queue = queueMapper.getByName(queueName);
            final double memWillUse = queue.getMemoryInUse() + memoryApply;
            final double cpuWillUse = queue.getMemoryInUse() + cpuApply;
            if (memWillUse >= queue.getMemoryMax() || cpuWillUse >= queue.getCpuMax()) {
                return "";
            }

            if (memWillUse <= queue.getMemoryMin() && cpuWillUse <= queue.getCpuMin()) {
                Instance instance = new Instance();
                instance.setInstanceId(instanceId);
                instance.setQueueName(queueName);
                instance.setState(VersionState.PENDING.getState());

                queue.setMemoryInUse(memWillUse);
                queue.setCpuInUse(cpuWillUse);

                resourceService.queueAndInstanceUpdate(instance, queue);
                return queueName;
            }

            if ((memWillUse > queue.getMemoryMin() && memWillUse < queue.getMemoryMax())
                    || (cpuWillUse > queue.getCpuMin() && cpuWillUse < queue.getCpuMax())) {
                if (!scramble) {
                    return "";
                }

                return scramble(instanceId, queue, cpuApply, memoryApply);

            }

        } catch (Exception e) {
            log.error("resource allocator exception ", e);
        } finally {
            mutex.release();
        }

        return "";
    }

    /**
     * 3.争抢、尝试获取其他队列的锁、
     *  - 获取所有优先级比它低的队列名单
     *  - 尝试更新数据，成功的就完成了资源申请
     * 被抢资源的queue max-=apply
     * 抢到资源的queue in_use+=apply
     *
     * @param instanceId  申请争抢的实例
     * @param queue       申请争抢的队列
     * @param cpuApply    申请的cpu
     * @param memoryApply 申请的内存
     * @return 如果争抢成功就返回该queue的名字，用于归还资源
     * @throws Exception
     */
    private String scramble(long instanceId, Queue queue, double cpuApply, double memoryApply) throws Exception {
        List<Queue> lowPriorityQueues = queueMapper.getLowPriorityQueues(queue.getPriority(), cpuApply, memoryApply);

        for (Queue beScrambled : lowPriorityQueues) {
            InterProcessSemaphoreMutex otherQueueMutex = new InterProcessSemaphoreMutex(client, ResourceConstants.LOCK_QUEUE_PREFIX + beScrambled.getQueueName());

            try {
                if (otherQueueMutex.acquire(50, TimeUnit.MILLISECONDS)) {
                    log.info("get {} lock during scramble", beScrambled.getQueueName());
                    double value = memoryApply + beScrambled.getMemoryInUse();
                    if (beScrambled.getMemoryMax() > value) {
                        beScrambled.setMemoryMax(beScrambled.getMemoryMax() - memoryApply);
                        queue.setMemoryInUse(queue.getMemoryInUse() + memoryApply);

                        Instance instance = new Instance();
                        instance.setInstanceId(instanceId);
                        instance.setQueueName(beScrambled.getQueueName());
                        instance.setState(VersionState.PENDING.getState());

                        resourceService.resourceScramble(beScrambled, queue, instance);
                        return beScrambled.getQueueName();
                    }
                }
            } catch (Exception e) {
                log.error("scramble resource exception ", e);
            } finally {
                otherQueueMutex.release();
            }

        }

        return "";

    }


}
