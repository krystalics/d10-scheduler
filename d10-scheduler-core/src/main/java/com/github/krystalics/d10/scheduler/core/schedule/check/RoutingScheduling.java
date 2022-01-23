package com.github.krystalics.d10.scheduler.core.schedule.check;

import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.constant.Pair;
import com.github.krystalics.d10.scheduler.common.constant.VersionState;
import com.github.krystalics.d10.scheduler.common.utils.SpringUtils;
import com.github.krystalics.d10.scheduler.dao.biz.VersionInstance;
import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import com.github.krystalics.d10.scheduler.resource.manager.ResourceScheduler;
import com.github.krystalics.d10.scheduler.resource.manager.common.ResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @author linjiabao001
 * @date 2022/1/9
 * @description 时间触发的任务检测
 */
public class RoutingScheduling implements ScheduledCheck {
    private static Logger log = LoggerFactory.getLogger(RoutingScheduling.class);

    private static SchedulerMapper schedulerMapper = SpringUtils.getBean(SchedulerMapper.class);
    private static TaskMapper taskMapper = SpringUtils.getBean(TaskMapper.class);
    private static JobInstance jobInstance = SpringUtils.getBean(JobInstance.class);
    private static ResourceScheduler resourceScheduler = SpringUtils.getBean(ResourceScheduler.class);

    private volatile boolean routingSchedulingStop = false;


    /**
     * 采用流的方式将任务分批取出、并给后续的处理步骤
     * 慢的地方采用流，快的地方采用批
     * 1。将时间到达的任务取出，并更新状态为waiting
     * 2。将
     */
    @Override
    public void start() throws InterruptedException {
        routingSchedulingStop = false;

        final Pair<Long, Long> taskIdScope = jobInstance.getTaskIds();
        log.info("routing scheduling check start! this scheduler's scope is {}", taskIdScope.toString());

        Date now = new Date();

        final List<VersionInstance> scheduleList = schedulerMapper.routingSchedulingInstances(taskIdScope.getL(), taskIdScope.getR());
        if (scheduleList != null && scheduleList.size() > 0) {
            log.info("get {} version instances to schedule", scheduleList.size());

            //todo 由于resource apply会有针对各个queueName的锁，所以后续的优化可以在依赖检查之后生成一个 <queueName,Instance>的map、再进行后续的资源申请
            final long count = scheduleList.parallelStream()
                    .filter(this::dependencyCheck)
                    .filter(this::resourceApply)
                    .filter(this::dispatch)
                    .count();

            log.info("{} instance has been dispatch to executor in this routing. and cost {} second", count, (System.currentTimeMillis() - now.getTime()) / 1000);
        } else {
            log.warn("nothing to schedule");
        }

    }

    /**
     * 对INIT状态进行依赖检查，检查所有的上游版本实例是否成功、其他状态直接跳过。
     * 这个方法过后，后续的处理中不存在 INIT 状态的instance
     */
    private boolean dependencyCheck(VersionInstance instance) {
        if (!routingSchedulingStop) {
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
        } else {
            throw new RuntimeException("interrupted");
        }


        return true;
    }

    /**
     * 对WAITING状态进行资源分配、其他状态直接跳过。
     * 这个方法过后，后续的处理中不存在 WAITING 状态的instance
     * <p>
     * 1.先对任务的并发度做限制，不能让整个系统就跑一个任务吧; 大致的限制就可以了，
     * 毕竟任务资源检查与后续任务运行中间间隔一些时间，可能会发现刷数的时候很多任务都通过了并发度验证，进入了运行阶段
     * 发现真正的运行数量远远超越了当初的限制；解决方案是：1。加本地缓存，通过了任务并发度限制的+1，用这个进行check 2。将这个数字记入mysql
     * 缓存存在着节点挂掉，在另一个节点无法恢复的风险，还是选择方案2：
     * <p>
     * 2.再对任务的队列资源进行申请
     */
    private boolean resourceApply(VersionInstance instance) {
        if (!routingSchedulingStop) {
            try {
                if (instance.getState().equals(VersionState.WAITING.getState())) {
                    int cnt = taskMapper.updateTaskConcurrentOccupation(instance.getTaskId());
                    //结果为0说明没有更新成功，没有占到资源
                    if (cnt == 0) {
                        log.warn("task concurrent occupation is fulled! please increase the concurrency");
                        return false;
                    }

                    String queueName = resourceScheduler.resourceAllocator(instance.getInstanceId(), instance.getQueueName(), instance.getCpuAvg(), instance.getMemoryAvg());
                    if (ResourceConstants.EMPTY_QUEUE.equals(queueName)) {
                        log.warn("{} has not enough resource!", queueName);
                        return false;
                    }
                    log.info("resource apply success! task queue = {},and finally the instance queue ={}", instance.getQueueName(), queueName);
                    return true;
                }
            } catch (Exception e) {
                log.error("something error happened in resource check when instance = {},caused by ", instance, e);
                return false;
            }

        } else {
            throw new RuntimeException("interrupted");
        }

        return true;
    }

    private boolean dispatch(VersionInstance instance) {
        if (!routingSchedulingStop) {
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
        } else {
            throw new RuntimeException("interrupted");
        }


        return false;
    }

    @Override
    public void stop() {
        routingSchedulingStop = true;
        log.error("routing scheduling stop");
    }

    @Override
    public boolean checkStop() {
        return true;
    }
}
