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
public class RoutingSchedulingCheck implements ScheduledCheck {
    private static Logger log = LoggerFactory.getLogger(RoutingSchedulingCheck.class);

    private static SchedulerMapper schedulerMapper = SpringUtils.getBean(SchedulerMapper.class);
    private static JobInstance jobInstance = SpringUtils.getBean(JobInstance.class);

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
                    .map(this::dependencyCheck)
                    .map(this::resourceCheck)
                    .count();


        } else {
            log.warn("nothing to schedule");
        }


    }

    private VersionInstance dependencyCheck(VersionInstance instance) {
        if (instance.getState().equals(VersionState.INIT.getState())) {
            final int count = schedulerMapper.checkUpInstancesAreSuccess(instance.getInstanceId());
            if (count == 0) {
                instance.setState(VersionState.WAITING.getState());
                log.info("{} 's all up instances are already success! update its state to wait", instance.getInstanceId());
                schedulerMapper.updateState(VersionState.WAITING.getState(), instance.getInstanceId());
            }
        }
        return instance;
    }

    private VersionInstance resourceCheck(VersionInstance instance) {
        if (instance.getState().equals(VersionState.WAITING.getState())) {
            //todo 获取资源并且 更新状态为 pending
        }
        return instance;
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
