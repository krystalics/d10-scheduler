package com.github.krystalics.d10.scheduler.start.event;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @Author linjiabao001
 * @Date 2022/2/11
 * @Description
 */
public class EventThreadPool {
    private final static ExecutorService service = new ThreadPoolExecutor(CommonConstants.CPU_NUM, CommonConstants.CPU_NUM * 2, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(50));

    private final static ConcurrentHashMap<EventType, Consumer<String>> eventProcessCache = new ConcurrentHashMap<>();

    public static Future<?> submit(Runnable runnable) {
        return service.submit(runnable);
    }

    public static void shutdown() {
        service.shutdown();
    }

    public static void shutdownNow() {
        service.shutdownNow();
    }

    public static void register(EventType type, Consumer<String> consumer) {
        eventProcessCache.put(type, consumer);
    }

    public static Consumer<String> processor(EventType type) {
        return eventProcessCache.get(type);
    }



}
