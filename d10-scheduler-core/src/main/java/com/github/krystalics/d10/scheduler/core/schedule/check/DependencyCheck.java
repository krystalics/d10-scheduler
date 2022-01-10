package com.github.krystalics.d10.scheduler.core.schedule.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author linjiabao001
 * @date 2022/1/9
 * @description 用作任务的依赖检查
 */
public class DependencyCheck implements ScheduledCheck {
    private static Logger log = LoggerFactory.getLogger(DependencyCheck.class);

    @Override
    public void start() {
        log.info("dependency check start");

    }

    @Override
    public void stop() {
        log.error("dependency check stop");

    }
}
