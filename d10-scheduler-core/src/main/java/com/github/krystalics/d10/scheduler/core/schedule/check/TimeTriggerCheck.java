package com.github.krystalics.d10.scheduler.core.schedule.check;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.constant.Pair;
import com.github.krystalics.d10.scheduler.common.constant.VersionState;
import com.github.krystalics.d10.scheduler.common.utils.SpringUtils;
import com.github.krystalics.d10.scheduler.core.pool.ScheduleExecutors;
import com.github.krystalics.d10.scheduler.dao.biz.VersionInstance;
import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author linjiabao001
 * @date 2022/1/9
 * @description 时间触发的任务检测
 */
public class TimeTriggerCheck implements ScheduledCheck {
    private static Logger log = LoggerFactory.getLogger(TimeTriggerCheck.class);

    private static SchedulerMapper schedulerMapper = SpringUtils.getBean(SchedulerMapper.class);
    private static JobInstance jobInstance = SpringUtils.getBean(JobInstance.class);

    private CountDownLatch latch;

    /**
     * 1。将时间到达的任务取出，并更新状态为waiting
     * 2。将
     */
    @Override
    public void start() throws InterruptedException {
        timeTrigger();

    }

    private void timeTrigger() {
        log.info("time check start!");
        final Pair<Long, Long> taskIds = jobInstance.getTaskIds();
        log.info("this scheduler's scope is {}", taskIds.toString());


        List<VersionInstance> scheduleList = schedulerMapper.readCouldTimeTriggered(taskIds.getL(), taskIds.getR());
        if (scheduleList != null && scheduleList.size() > 0) {

            log.info("get {} version instances to schedule", scheduleList.size());
            final List<List<VersionInstance>> partitions = Lists.partition(scheduleList, (scheduleList.size() / CommonConstants.INSTANCE_PER_PARTITION) + 1);

            latch = new CountDownLatch(partitions.size());


            partitions.forEach(list -> {
                ScheduleExecutors.submitTimeCheck(() -> {
                    list.forEach(item -> item.setState(VersionState.WAITING.getState()));
                    schedulerMapper.batchUpdateState(list);
                    latch.countDown();
                });
            });
        } else {
            log.warn("nothing to schedule");
        }
    }

    private void dependencyCheck() {
        log.info("dependency check start!");
        final Pair<Long, Long> taskIds = jobInstance.getTaskIds();
        log.info("this scheduler's scope is {}", taskIds.toString());


        List<VersionInstance> scheduleList = schedulerMapper.readNeedPreparedTasks(taskIds.getL(), taskIds.getR(), VersionState.INIT.getState());
        if (scheduleList != null && scheduleList.size() > 0) {

            log.info("get {} version instances to schedule", scheduleList.size());
            final List<List<VersionInstance>> partitions = Lists.partition(scheduleList, (scheduleList.size() / CommonConstants.INSTANCE_PER_PARTITION) + 1);

            latch = new CountDownLatch(partitions.size());

            partitions.forEach(list -> {
                ScheduleExecutors.submitTimeCheck(() -> {
                    for (VersionInstance versionInstance : list) {
                        final int count = schedulerMapper.checkUpInstanceIsSuccess(versionInstance.getInstanceId());
                        if (count == 0) {
                            log.info("{} 's all up instances are already success! update its state to wait",versionInstance.getInstanceId());
                            schedulerMapper.updateStateToWait(versionInstance.getInstanceId());
                        }
                    }
                    latch.countDown();
                });
            });
        } else {
            log.warn("nothing to schedule");
        }
    }

    private void resourceCheck() {
        log.info("resource check start!");
        final Pair<Long, Long> taskIds = jobInstance.getTaskIds();
        log.info("this scheduler's scope is {}", taskIds.toString());


        List<VersionInstance> scheduleList = schedulerMapper.readNeedPreparedTasks(taskIds.getL(), taskIds.getR(), VersionState.WAITING.getState());
        if (scheduleList != null && scheduleList.size() > 0) {

            log.info("get {} version instances to schedule", scheduleList.size());
            final List<List<VersionInstance>> partitions = Lists.partition(scheduleList, (scheduleList.size() / CommonConstants.INSTANCE_PER_PARTITION) + 1);

            latch = new CountDownLatch(partitions.size());

            partitions.forEach(list -> {
                ScheduleExecutors.submitTimeCheck(() -> {
                    for (VersionInstance versionInstance : list) {
                        //todo 1.check这个instance的队列使用的资源有没有达到min
                        //todo 2.有的话尝试更新；没有的话，尝试开启争抢模式，去争抢同优先级队列或者之下的 资源
                        //todo 3.获取资源后更新自身的状态
                        //todo 4.这是一个事务
                        final int count = schedulerMapper.checkUpInstanceIsSuccess(versionInstance.getInstanceId());
                        if (count == 0) {
                            log.info("{} 's all up instances are already success! update its state to wait",versionInstance.getInstanceId());
                            schedulerMapper.updateStateToWait(versionInstance.getInstanceId());
                        }
                    }
                    latch.countDown();
                });
            });
        } else {
            log.warn("nothing to schedule");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    void resource(VersionInstance versionInstance){

    }

    @Override
    public void stop() {
        log.error("time check stop");
    }

    @Override
    public boolean checkStop() {
        return true;
    }
}
