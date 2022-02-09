package com.github.krystalics.d10.scheduler.executor.worker;

import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;

import java.util.concurrent.*;

/**
 * @Author linjiabao001
 * @Date 2022/2/7
 * @Description 运行时线程池
 */
public class InstanceRunPool {
    private static final ExecutorService service = new ThreadPoolExecutor(Integer.MAX_VALUE, Integer.MAX_VALUE, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(1));
    private static final ConcurrentHashMap<Long, Future<?>> pool = new ConcurrentHashMap<>();

    public static void add(VersionInstance instance) {
        Future<?> future = submit(new InstanceRunWorker(instance));
        pool.put(instance.getInstanceId(), future);
    }

    private static Future<?> submit(Runnable runnable) {
        return service.submit(runnable);
    }

    public static boolean cancel(long instanceId) {
        Future<?> future = pool.get(instanceId);
        return future.cancel(true);
    }

    public static void shutdown() {
        service.shutdown();
    }

    public static void shutdownNow() {
        service.shutdownNow();
    }
}
