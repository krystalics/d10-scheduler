package com.github.krystalics.d10.scheduler.init.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author linjiabao001
 * @date 2020/5/10
 * @description
 */
public class StartExecutor {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
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
