package com.github.krystalics.d10.scheduler.core.schedule.check;

import com.github.krystalics.d10.scheduler.common.constant.*;
import com.github.krystalics.d10.scheduler.common.utils.SpringUtils;
import com.github.krystalics.d10.scheduler.core.exception.StopException;
import com.github.krystalics.d10.scheduler.core.service.SchedulerService;
import com.github.krystalics.d10.scheduler.core.service.impl.TransactionService;
import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
import com.github.krystalics.d10.scheduler.resource.manager.ResourceScheduler;
import com.github.krystalics.d10.scheduler.resource.manager.common.ResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author linjiabao001
 * @date 2022/1/9
 * @description 任务调度的主体程序,
 * keypoint 在实践中系统中超过3天还未运行到终态的实例就不管了,属于垃圾数据
 */
public class RoutingScheduling implements ScheduledCheck {
    private static final Logger log = LoggerFactory.getLogger(RoutingScheduling.class);

    private static final SchedulerMapper schedulerMapper = SpringUtils.getBean(SchedulerMapper.class);
    private static final SchedulerService schedulerService = SpringUtils.getBean(SchedulerService.class);

    private static final ResourceScheduler resourceScheduler = SpringUtils.getBean(ResourceScheduler.class);
    private static final TransactionService transactionService = SpringUtils.getBean(TransactionService.class);

    private volatile boolean routingSchedulingStop = false;


    /**
     * 1.将达到运行时间的任务取出
     * 2.校验依赖是否完成   -并发处理
     * 3.校验任务并发度    -并发处理
     * 4.由于资源竞争比较激烈，采用了悲观锁。所以按照queueName进行分桶、串行处理
     * 5.分发的时候再次进行 并发处理
     */
    @Override
    public void start() throws InterruptedException {
        routingSchedulingStop = false;

        Date now = new Date();
        final List<VersionInstance> scheduleList = schedulerService.fetchData(ScheduledEnum.SCHEDULING, now);

        if (scheduleList != null && scheduleList.size() > 0) {
            log.info("get {} version instances to schedule", scheduleList.size());

            Map<String, List<VersionInstance>> queueInstancesMap = new ConcurrentHashMap<>();
            List<VersionInstance> dispatchStandBy = new ArrayList<>();

            List<VersionInstance> taskCondition = scheduleList.parallelStream()
                    .filter(this::dependencyCheck)
                    .filter(this::concurrencyApply)
                    .collect(Collectors.toList());

            for (VersionInstance instance : taskCondition) {
                List<VersionInstance> list = queueInstancesMap.getOrDefault(instance.getQueueName(), new ArrayList<>());
                list.add(instance);
                queueInstancesMap.put(instance.getQueueName(), list);
            }

            queueInstancesMap.forEach((k, v) -> {
                List<VersionInstance> list = v.stream()
                        .filter(this::resourceApply)
                        .collect(Collectors.toList());
                dispatchStandBy.addAll(list);
            });

            long count = dispatchStandBy.parallelStream()
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
                        instance.setState(VersionState.WAITING_CON.getState());
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
            throw new StopException("scheduling has been interrupted");
        }


        return true;
    }

    /**
     * 对任务的并发度做限制，不能让整个系统就跑一个任务吧; 大致的限制就可以了，毕竟任务资源检查与后续任务运行中间间隔一些时间，
     * 可能会发现刷数的时候很多任务都通过了并发度验证，进入了运行阶段才发现真正的运行数量远远超越了当初的限制；
     * 解决方案是：1。加本地缓存，通过了任务并发度限制的+1，用这个进行check 2。将这个数字记入mysql
     * 缓存存在着节点挂掉，在另一个节点无法恢复的风险，还是选择了第二个方案
     *
     * @param instance 需要处理的instance
     * @return 更新成功则通过
     */
    private boolean concurrencyApply(VersionInstance instance) {
        if (!routingSchedulingStop) {
            try {
                if (instance.getState().equals(VersionState.WAITING_CON.getState())) {
                    transactionService.updateTaskAndInstance(instance.getTaskId(), instance.getInstanceId());
                    instance.setState(VersionState.WAITING_RES.getState());
                    log.info("task {} concurrent occupation success!", instance.getTaskId());
                    return true;
                }
            } catch (Exception e) {
                log.error("something error happened in resource check when instance = {},caused by ", instance, e);
                return false;
            }

        } else {
            throw new StopException("scheduling has been interrupted");
        }

        return true;
    }

    /**
     * 2.对任务的队列资源进行申请
     */
    private boolean resourceApply(VersionInstance instance) {
        if (!routingSchedulingStop) {
            try {
                if (instance.getState().equals(VersionState.WAITING_RES.getState())) {
                    String queueName = resourceScheduler.resourceAllocator(instance.getInstanceId(), instance.getQueueName(), instance.getCpuAvg(), instance.getMemoryAvg());
                    if (ResourceConstants.EMPTY_QUEUE.equals(queueName)) {
                        log.warn("{} has not enough resource!", queueName);
                        return false;
                    }
                    instance.setState(VersionState.PENDING.getState());
                    log.info("resource apply success! task queue = {},and finally the instance queue ={}", instance.getQueueName(), queueName);
                    return true;
                }
            } catch (Exception e) {
                log.error("something error happened in resource check when instance = {},caused by ", instance, e);
                return false;
            }

        } else {
            throw new StopException("scheduling has been interrupted");
        }

        return true;
    }

    private boolean dispatch(VersionInstance instance) {
        if (!routingSchedulingStop) {
            try {
                if (instance.getState().equals(VersionState.PENDING.getState())) {
                    schedulerService.dispatch(instance);
                    log.info("dispatch success! instanceId = {}", instance.getInstanceId());
                    return true;
                }

            } catch (Exception e) {
                log.error("something error happened in dispatch when instance = {},caused by ", instance, e);
            }
        } else {
            throw new StopException("scheduling has been interrupted");
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
        return routingSchedulingStop;
    }
}
