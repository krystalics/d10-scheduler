package com.github.krystalics.d10.scheduler.core.schedule.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author linjiabao001
 * @Date 2022/1/10 11:31
 * @Description TODO
 */
public class RunningCheck implements ScheduledCheck{
    private static Logger log = LoggerFactory.getLogger(RunningCheck.class);

    @Override
    public void start() {
        log.info("running check start");

    }

    @Override
    public void stop() {
        log.error("running check stop");

    }
}
