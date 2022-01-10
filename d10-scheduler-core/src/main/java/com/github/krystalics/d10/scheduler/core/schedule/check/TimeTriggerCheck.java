package com.github.krystalics.d10.scheduler.core.schedule.check;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.constant.Pair;
import com.github.krystalics.d10.scheduler.common.utils.SpringUtils;
import com.github.krystalics.d10.scheduler.core.pool.ScheduleExecutors;
import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author linjiabao001
 * @date 2022/1/9
 * @description 时间触发的任务检测
 */
public class TimeTriggerCheck implements ScheduledCheck {
    private static Logger log = LoggerFactory.getLogger(TimeTriggerCheck.class);

    private static SchedulerMapper schedulerMapper = SpringUtils.getBean(SchedulerMapper.class);
    private static JobInstance jobInstance = SpringUtils.getBean(JobInstance.class);

    /**
     * 1。将本调度器分片范围的可运行任务取出
     * 2。设置为pending状态
     */
    @Override
    public void start() {
        log.info("time check start!");
        final Pair<Long, Long> taskIds = jobInstance.getTaskIds();
        log.info("this scheduler's scope is {}", taskIds.toString());

        List<Long> scheduleList = schedulerMapper.schedulerReadVersionInstance(taskIds.getL(), taskIds.getR());
        if (scheduleList != null && scheduleList.size() > 0) {
            log.info("get {} version instances to schedule", scheduleList.size());
            final List<List<Long>> partitions = Lists.partition(scheduleList, (scheduleList.size() / CommonConstants.INSTANCE_PER_PARTITION) + 1);

            partitions.forEach(list -> {
                ScheduleExecutors.submitTimeCheck(() -> {
                    //todo update the state to waiting resource
                });
            });

        } else {
            log.warn("nothing to schedule");
        }
    }

    @Override
    public void stop() {
        log.error("time check stop");
    }

}
