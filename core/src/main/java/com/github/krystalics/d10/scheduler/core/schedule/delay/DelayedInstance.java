package com.github.krystalics.d10.scheduler.core.schedule.delay;


import com.github.krystalics.d10.scheduler.dao.entity.Instance;
import com.google.common.base.Objects;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author linjiabao001
 * @date 2020/4/12
 * @description 进入时间触发队列中，priority属性是为了分发时用于排序的
 */
public class DelayedInstance implements Delayed {
    private Instance instance;
    private long startTime;
    private int priority;


    public DelayedInstance(Instance instance, long delayInMilliseconds, int priority) {
        this.instance = instance;
        this.startTime = delayInMilliseconds;
        this.priority = priority;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.startTime - ((DelayedInstance) o).getStartTime());
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "DelayedObject{" +
                "data='" + instance + '\'' +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DelayedInstance)) {
            return false;
        }
        DelayedInstance that = (DelayedInstance) o;
        return getStartTime() == that.getStartTime() &&
                getPriority() == that.getPriority() &&
                Objects.equal(getInstance(), that.getInstance());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getInstance(), getStartTime(), getPriority());
    }
}
