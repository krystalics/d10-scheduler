package com.github.krystalics.d10.scheduler.dao.biz;

import lombok.Data;

import java.util.Date;

/**
 * @author linjiabao001
 * @date 2022/1/3
 * @description 属于运行中的实例，是底层version表与instance表的聚合
 * @see com.github.krystalics.d10.scheduler.dao.entity.Version
 * @see com.github.krystalics.d10.scheduler.dao.entity.Instance
 * <p>
 */
@Data
public class VersionInstance {
    private Long taskId;
    private Long versionId;
    private Long instanceId;
    private String versionNo;
    private Integer retryRemainTimes;
    private Date startTimeTheory;
    private String jobConf;
    private Integer state;
    private String jobLogAddress;
    private Date runStartTime;
    private Date runEndTime;
    private String node;
    private Integer autoStart;
    private String queueName;
    private String queueInYarn;
    private Double cpuAvg;
    private Double memoryAvg;
    private Date ctime;
    private Date mtime;
}
