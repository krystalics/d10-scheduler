package com.github.krystalics.d10.scheduler.core.schedule.check;

import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.constant.Pair;
import com.github.krystalics.d10.scheduler.common.constant.VersionState;
import com.github.krystalics.d10.scheduler.common.utils.SpringUtils;
import com.github.krystalics.d10.scheduler.dao.biz.VersionInstance;
import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
import com.github.krystalics.d10.scheduler.resource.manager.ResourceScheduler;
import com.github.krystalics.d10.scheduler.resource.manager.common.ResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author linjiabao001
 * @date 2022/1/9
 * @description 时间触发的任务检测
 */
public class RoutingScheduling implements ScheduledCheck {
    private static Logger log = LoggerFactory.getLogger(RoutingScheduling.class);

    private static SchedulerMapper schedulerMapper = SpringUtils.getBean(SchedulerMapper.class);
    private static JobInstance jobInstance = SpringUtils.getBean(JobInstance.class);
    private static ResourceScheduler resourceScheduler = SpringUtils.getBean(ResourceScheduler.class);

    private CountDownLatch latch;

    /**
     * 采用流的方式将任务分批取出、并给后续的处理步骤
     * 慢的地方采用流，快的地方采用批
     * 1。将时间到达的任务取出，并更新状态为waiting
     * 2。将
     */
    @Override
    public void start() throws InterruptedException {
        log.info("routing scheduling check start!");
        final Pair<Long, Long> taskIdScope = jobInstance.getTaskIds();
        log.info("this scheduler's scope is {}", taskIdScope.toString());
        final List<VersionInstance> scheduleList = schedulerMapper.routingSchedulingInstances(taskIdScope.getL(), taskIdScope.getR());
        if (scheduleList != null && scheduleList.size() > 0) {
            log.info("get {} version instances to schedule", scheduleList.size());

            final long count = scheduleList.parallelStream()
                    .filter(this::dependencyCheck)
                    .filter(this::resourceApply)
                    .filter(this::dispatch)
                    .count();

            log.info("{} instance has been dispatch to executor in this routing.", count);

        } else {
            log.warn("nothing to schedule");
        }

    }

    /**
     * 对INIT状态进行依赖检查，检查所有的上游版本实例是否成功、其他状态直接跳过。
     * 这个方法过后，后续的处理中不存在 INIT 状态的instance
     */
    private boolean dependencyCheck(VersionInstance instance) {
        try {
            if (instance.getState().equals(VersionState.INIT.getState())) {
                final int count = schedulerMapper.checkUpInstancesAreSuccess(instance.getInstanceId());
                if (count == 0) {
                    instance.setState(VersionState.WAITING.getState());
                    log.info("instanceId = {} 's all up instances are already success! update its state to wait", instance.getInstanceId());
                    schedulerMapper.updateInstance(instance);
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            log.error("something error happened in dependency check when instance = {},caused by ", instance, e);
            return false;
        }


        return true;
    }

    /**
     * 对WAITING状态进行资源分配、其他状态直接跳过。
     * 这个方法过后，后续的处理中不存在 WAITING 状态的instance
     */
    private boolean resourceApply(VersionInstance instance) {
        try {
            if (instance.getState().equals(VersionState.WAITING.getState())) {
                String queueName = resourceScheduler.resourceAllocator(instance.getInstanceId(), instance.getQueueName(), instance.getCpuAvg(), instance.getMemoryAvg());
                if (ResourceConstants.EMPTY_QUEUE.equals(queueName)) {
                    return false;
                }
                log.info("resource apply success! task queue = {},and finally the instance queue ={}", instance.getQueueName(), queueName);
                return true;
            }
        } catch (Exception e) {
            log.error("something error happened in resource check when instance = {},caused by ", instance, e);
            return false;
        }

        return true;
    }

    private boolean dispatch(VersionInstance instance) {
        try {
            if (instance.getState().equals(VersionState.PENDING.getState())) {
                //todo 分发任务到executor

                log.info("dispatch success! instanceId = {}", instance.getInstanceId());
                instance.setState(VersionState.RUNNING.getState());
                schedulerMapper.updateInstance(instance);
                return true;
            }

        } catch (Exception e) {
            log.error("something error happened in dispatch when instance = {},caused by ", instance, e);
        }

        return false;
    }

    @Override
    public void stop() {
        log.error("routing scheduling stop");
    }

    @Override
    public boolean checkStop() {
        return true;
    }
}
