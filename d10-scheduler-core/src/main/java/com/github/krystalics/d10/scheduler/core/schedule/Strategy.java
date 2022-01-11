package com.github.krystalics.d10.scheduler.core.schedule;

public enum Strategy {
    CHECK("ScheduledCheck");
    private final String value;

    Strategy(String value) {
        this.value = value;
    }
}