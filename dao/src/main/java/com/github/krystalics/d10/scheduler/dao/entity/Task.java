package com.github.krystalics.d10.scheduler.dao.entity;

import lombok.Data;

/**
 * @author linjiabao001
 * @date 2021/10/2
 * @description
 */
@Data
public class Task {
    private int taskId;
    private String taskName;
    private int type;
    /**
     * @see com.github.krystalics.d10.scheduler.core.common.FrequencyGranularity
     */
    private int frequency;
    /**
     * @see  com.github.krystalics.d10.scheduler.core.common.ScheduleType
     */
    private int scheduleType;
    private int concurrency;
    private String crontab;
    private String jobConf;
    private String ownerMail;
    private String creatorMail;

    private int retryTimes;
    /**
     * 任务状态,1表示待上线，2表示正常，3表示已下线. 缺省为1
     */
    private int state;
    private int priority;
}
