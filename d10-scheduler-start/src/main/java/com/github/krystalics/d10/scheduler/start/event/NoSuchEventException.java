package com.github.krystalics.d10.scheduler.start.event;

/**
 * @Author linjiabao001
 * @Date 2022/2/11
 * @Description
 */
public class NoSuchEventException extends RuntimeException {
    public NoSuchEventException(String message) {
        super(message);
    }
}
