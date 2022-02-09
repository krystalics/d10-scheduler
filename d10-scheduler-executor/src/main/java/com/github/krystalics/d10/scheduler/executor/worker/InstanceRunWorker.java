package com.github.krystalics.d10.scheduler.executor.worker;

import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import com.github.krystalics.d10.scheduler.executor.utils.ClearUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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

    /**
     * 1.运行之前先check下在yarn或者k8s上是否有对应的任务存在，如果存在kill掉后再拉起任务
     * 2.创建日志的目录
     * 3.执行具体类型的任务
     * 4.中断处理
     *
     * todo 选择source与target模式吗？
     */
    @SneakyThrows
    @Override
    public void run() {
        log.info("instance is running:{}", instance);
        ClearUtils.killRemoteApp(instance.getRemoteId());
        FileUtils.forceMkdirParent(new File(instance.getJobLogAddress()));


    }
}
