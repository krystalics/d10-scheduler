package com.github.krystalics.d10.scheduler.executor.worker;

import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import com.github.krystalics.d10.scheduler.executor.utils.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author linjiabao001
 * @Date 2022/2/7
 * @Description
 */
public class InstanceRunWorker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(InstanceRunWorker.class);

    private final VersionInstance instance;

    public InstanceRunWorker(VersionInstance instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        log.info("instance is running:{}", instance);
        //todo 具体的执行步骤
    }
}
