package com.github.krystalics.d10.scheduler.core.schedule.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author linjiabao001
 * @date 2020/5/4
 * @description 目前一共两种消费者
 * 1：从延迟队列中取实例到分发队列中 ：数量 2
 * 2：从分发队列取实例到执行线程池中 ：数量 3
 */
public class ConsumerExecutors {
    public static final int MAX_CONSUMERS_NUM = 5;
    public static final int DELAYED_CONSUMERS_NUM = 2;
    public static final int SCHEDULED_CONSUMERS_NUM = 3;

    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executorService = new ThreadPoolExecutor(MAX_CONSUMERS_NUM, MAX_CONSUMERS_NUM, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public static Future<?> submit(Runnable runnable) {
        return executorService.submit(runnable);
    }

    public static void shutdown() {
        executorService.shutdown();
    }

    public static void shutdownNow() {
        executorService.shutdownNow();
    }
}
