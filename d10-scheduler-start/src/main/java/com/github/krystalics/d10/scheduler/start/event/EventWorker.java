package com.github.krystalics.d10.scheduler.start.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * @Author linjiabao001
 * @Date 2022/2/11
 * @Description
 */
public class EventWorker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(EventWorker.class);

    private final EventType eventType;
    private final String param;

    public EventWorker(EventType eventType, String param) {
        this.eventType = eventType;
        this.param = param;
    }

    @Override
    public void run() {
        log.info("process event type = {},param = {}", eventType, param);

        Consumer<String> processor = EventThreadPool.processor(eventType);
        if (processor == null) {
            throw new NoSuchEventException("eventType " + eventType + " is not exit");
        }

        processor.accept(param);
    }
}
