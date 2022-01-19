package com.github.krystalics.d10.scheduler.core.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author linjiabao001
 * @date 2022/1/9
 * @description
 */
public class ScheduleExecutors {
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService routingScheduling = new ThreadPoolExecutor(CPU_NUM, CPU_NUM * 2, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private static final ExecutorService dependencyCheckers = new ThreadPoolExecutor(10, CPU_NUM * 2, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private static final ExecutorService resourceCheckers = new ThreadPoolExecutor(10, CPU_NUM * 2, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private static final ExecutorService runningCheckers = new ThreadPoolExecutor(10, CPU_NUM * 2, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public static Future<?> submitTimeCheck(Runnable runnable) {
        return routingScheduling.submit(runnable);
    }

    public static void shutdownTimeCheck() {
        routingScheduling.shutdown();
    }

    public static void shutdownNowTimeCheck() {
        routingScheduling.shutdownNow();
    }

    public static Future<?> submitDependencyCheck(Runnable runnable) {
        return dependencyCheckers.submit(runnable);
    }

    public static void shutdownDependencyCheck() {
        dependencyCheckers.shutdown();
    }

    public static void shutdownNowDependencyCheck() {
        dependencyCheckers.shutdownNow();
    }

    public static Future<?> submitResourceCheck(Runnable runnable) {
        return resourceCheckers.submit(runnable);
    }

    public static void shutdownResourceCheck() {
        resourceCheckers.shutdown();
    }

    public static void shutdownNowResourceCheck() {
        resourceCheckers.shutdownNow();
    }

    public static Future<?> submitRunningCheck(Runnable runnable) {
        return runningCheckers.submit(runnable);
    }

    public static void shutdownRunningCheck() {
        runningCheckers.shutdown();
    }

    public static void shutdownNowRunningCheck() {
        runningCheckers.shutdownNow();
    }

    public static void shutdown() {
        shutdownTimeCheck();
        shutdownDependencyCheck();
        shutdownResourceCheck();
        shutdownRunningCheck();
    }

    public static void shutdownNow() {
        shutdownNowTimeCheck();
        shutdownNowDependencyCheck();
        shutdownNowResourceCheck();
        shutdownNowRunningCheck();
    }
}
