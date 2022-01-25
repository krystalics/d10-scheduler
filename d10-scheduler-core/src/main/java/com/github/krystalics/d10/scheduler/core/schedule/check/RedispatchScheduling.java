package com.github.krystalics.d10.scheduler.core.schedule.check;

import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.constant.Pair;
import com.github.krystalics.d10.scheduler.common.utils.SpringUtils;
import com.github.krystalics.d10.scheduler.dao.biz.VersionInstance;
import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @Author linjiabao001
 * @Date 2022/1/10 11:32
 * @Description 对状态为失败以及executor节点失败上的任务进行重新的分发
 */
public class RedispatchScheduling implements ScheduledCheck {
    private static Logger log = LoggerFactory.getLogger(RedispatchScheduling.class);

    private static SchedulerMapper schedulerMapper = SpringUtils.getBean(SchedulerMapper.class);
    private static JobInstance jobInstance = SpringUtils.getBean(JobInstance.class);
    private volatile boolean redispatchSchedulingStop = false;

    @Override
    public void start() {
        redispatchSchedulingStop = false;
        final Pair<Long, Long> taskIdScope = jobInstance.getTaskIds();
        log.info("redispatch scheduling start! this scheduler's scope is {}", taskIdScope.toString());

        Date now = new Date();
        //todo 改成获取需要重新调度的instance
        final List<VersionInstance> scheduleList = schedulerMapper.routingSchedulingInstances(taskIdScope.getL(), taskIdScope.getR());
        if (scheduleList != null && scheduleList.size() > 0) {

        }
    }

    @Override
    public void stop() {
        redispatchSchedulingStop = true;
        log.warn("redispatch check stop");

    }

    @Override
    public boolean checkStop() {
        return true;
    }
}
