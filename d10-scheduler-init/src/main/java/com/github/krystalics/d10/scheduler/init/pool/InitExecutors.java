package com.github.krystalics.d10.scheduler.init.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author linjiabao001
 * @date 2020/4/27
 * @description
 * IO 密集型任务：由于线程并不是一直在运行，所以可以尽可能的多配置线程，比如 CPU 个数 * 2
 * CPU 密集型任务（大量复杂的运算）应当分配较少的线程，比如 CPU 个数相当的大小。
 * <p>
 * 初始化版本和实例，需要大量插入数据库等操作，所以线程池的数量 定义为CPU个数*2
 * 非核心线程保持30s的活性
 */
public class InitExecutors {
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executorService = new ThreadPoolExecutor(CPU_NUM, CPU_NUM * 2, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public static Future<?> submit(Runnable runnable){
        return executorService.submit(runnable);
    }

    public static void shutdown(){
        executorService.shutdown();
    }

    public static void shutdownNow(){
        executorService.shutdownNow();
    }

}
