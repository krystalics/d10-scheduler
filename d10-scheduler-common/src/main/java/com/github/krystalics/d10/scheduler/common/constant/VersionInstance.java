package com.github.krystalics.d10.scheduler.common.constant;

import lombok.Data;

import java.util.Date;

/**
 * @author linjiabao001
 * @date 2022/1/3
 * @description 属于运行中的实例，是底层version表与instance表的聚合
 * see com.github.krystalics.d10.scheduler.dao.entity.Version
 * com.github.krystalics.d10.scheduler.dao.entity.Instance
 * <p>
 * 按照priority降序排列,biz_priority降序排列
 */
@Data
public class VersionInstance implements Comparable<VersionInstance> {
    private Long taskId;
    private Long versionId;
    private Long instanceId;
    private Integer type;
    private String versionNo;
    private Integer retryRemainTimes;
    private Date startTimeTheory;
    private String jobConf;
    private Integer state;
    private String jobLogAddress;
    private Date runStartTime;
    private Date runEndTime;
    private String node;
    private Integer remoteId;
    private Integer autoStart;
    private String queueName;
    private String queueInYarn;
    private Integer priority;
    private Integer bizPriority;
    private Double cpuAvg;
    private Double memoryAvg;
    private Date ctime;
    private Date mtime;

    @Override
    public int compareTo(VersionInstance o) {
        if (this.priority.equals(o.priority)) {
            return o.bizPriority - this.bizPriority;
        }
        return o.priority - this.priority;
    }
}
