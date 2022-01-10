package com.github.krystalics.d10.scheduler.core.schedule.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author linjiabao001
 * @date 2022/1/9
 * @description
 */
public class ResourceCheck implements ScheduledCheck {
    private static Logger log = LoggerFactory.getLogger(ResourceCheck.class);

    @Override
    public void start() {
        log.info("resource check start");

    }

    @Override
    public void stop() {
        log.error("resource check stop");

    }
}
