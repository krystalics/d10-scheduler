package com.github.krystalics.d10.scheduler.core.schedule.check;

import com.github.krystalics.d10.scheduler.common.constant.*;
import com.github.krystalics.d10.scheduler.common.utils.SpringUtils;
import com.github.krystalics.d10.scheduler.core.exception.StopException;
import com.github.krystalics.d10.scheduler.core.service.SchedulerService;
import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
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
    private static final Logger log = LoggerFactory.getLogger(RedispatchScheduling.class);

    private static final SchedulerService schedulerService = SpringUtils.getBean(SchedulerService.class);
    private volatile boolean redispatchSchedulingStop = false;

    @Override
    public void start() {
        redispatchSchedulingStop = false;

        Date now = new Date();

        final List<VersionInstance> scheduleList = schedulerService.fetchData(ScheduledEnum.REDISPATCH, now);
        if (scheduleList != null && scheduleList.size() > 0) {

            long count = 0;

            for (VersionInstance instance : scheduleList) {
                boolean dispatched = dispatch(instance);
                if (dispatched) {
                    count++;
                }
            }

            log.info("{} instance has been redispatch to executor in this round-trip. and cost {} second", count, (System.currentTimeMillis() - now.getTime()) / 1000);
        } else {
            log.warn("nothing to redispatch");
        }
    }


    private boolean dispatch(VersionInstance instance) {
        if (!redispatchSchedulingStop) {
            try {
                if (instance.getState().equals(VersionState.ReDispatch.getState())) {
                    schedulerService.dispatch(instance);
                    log.info("redispatch success! instanceId = {}", instance.getInstanceId());
                    return true;
                }

            } catch (Exception e) {
                log.error("something error happened in redispatch when instance = {},caused by ", instance, e);
            }
        } else {
            throw new StopException("rescheduling has been interrupted");
        }


        return false;
    }

    @Override
    public void stop() {
        redispatchSchedulingStop = true;
        log.warn("redispatch check stop");
    }

    @Override
    public boolean checkStop() {
        return redispatchSchedulingStop;
    }
}
