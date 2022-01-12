package com.github.krystalics.d10.scheduler.core.schedule.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author linjiabao001
 * @Date 2022/1/10 11:32
 * @Description TODO
 */
public class ReDispatchCheck implements ScheduledCheck{
    private static Logger log = LoggerFactory.getLogger(ReDispatchCheck.class);

    @Override
    public void start() {
        log.info("redispatch check start");

    }

    @Override
    public void stop() {
        log.error("redispatch check stop");

    }

    @Override
    public boolean checkStop() {
        return true;
    }
}
