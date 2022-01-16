package com.github.krystalics.d10.scheduler.dao.biz;

import lombok.Data;

import java.time.ZonedDateTime;

/**
 * @author linjiabao001
 * @date 2022/1/3
 * @description 属于运行中的实例，是底层version表与instance表的聚合
 * @see com.github.krystalics.d10.scheduler.dao.entity.Version
 * @see com.github.krystalics.d10.scheduler.dao.entity.Instance
 *
 * todo 取出默认是utc时间、或者可以把zone换一下
 */
@Data
public class VersionInstance {
    private Long taskId;
    private Long versionId;
    private Long instanceId;
    private String versionNo;
    private Integer retryRemainTimes;
    private ZonedDateTime startTimeTheory;
    private String jobConf;
    private Integer state;
    private String jobLogAddress;
    private ZonedDateTime runStartTime;
    private ZonedDateTime runEndTime;
    private String node;
    private Integer autoStart;
    private String queueName;
    private Double cpuAvg;
    private Double memoryAvg;
    private ZonedDateTime ctime;
    private ZonedDateTime mtime;
}
