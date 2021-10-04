package com.github.krystalics.d10.scheduler.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 任务表
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */
@Data
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */

    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型。1hive_sql2shell
     */
    private Integer type;

    /**
     * 数据频率单位,0表示一次性临时任务,1表示分钟,2表示小时,3日,4周,5月,缺省为0
     */
    private Integer frequency;

    /**
     * 0:表示时间触发，1:表示依赖触发，依赖触发没有固定的启动时间，crontab只是用来确定周期的
     */
    private Integer scheduleType;

    /**
     * 在调度中同时跑的 同一任务的并发数限制
     */
    private Integer concurrency;

    /**
     * quartz的crontab格式,空格分隔
     */
    private String crontab;

    /**
     * 命令配置
     */
    private String jobConf;


    /**
     * 任务归属人邮箱前缀
     */
    private String ownerMail;

    /**
     * 任务创建人邮箱前缀,如zhangjingyi
     */
    private String creatorMail;

    /**
     * 任务失败重试次数
     */
    private Integer retryTimes;

    /**
     * 任务状态,1表示上线 0表示下线.
     */
    private Integer state;

    /**
     * 任务备注
     */
    private String note;

    /**
     * 任务初始优先级
     */
    private Integer priority;

    private LocalDateTime ctime;

    /**
     * 记录最后更改时间
     */
    private LocalDateTime mtime;
}
