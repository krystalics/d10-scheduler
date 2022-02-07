package com.github.krystalics.d10.scheduler.dao.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 实例，任务实例
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 * todo instance 重新分发的时候找个字段记录一下之前的运行节点
 */
@Data
public class Instance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务实例id
     */
    private Long instanceId;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 数据版本id
     */
    private Long versionId;

    /**
     * 实例的调度类型:1时间调度 2事件调度.对于日常例行实例,与任务的调度类型一样.如果是手动修复且忽略上游,则应该是时间触发.
     */
    /**
     * 实例将被调起的 物理时间点,形如"20140802 10:30",根据crontab填入
     */
    private Date startTimeTheory;

    /**
     * 命令配置，已替换版本号变量等
     */
    private String jobConf;

    /**
     * 实例的状态: 1新建，2:等待 3:已分发 4:运行中 5:成功 6:失败 7:杀死
     */
    private Integer state;

    /**
     * 正常运行日志URL
     */
    private String jobLogAddress;

    /**
     * 实例运行开始时间
     */
    private Date runStartTime;

    /**
     * 实例运行结束时间
     */
    private Date runEndTime;

    /**
     * 实例执行节点
     */
    private String node;

    /**
     * 之前实例执行节点
     * todo 加到表中
     */
    private String nodeBefore;

    private Integer autoStart;

    /**
     * 客户端资源来自于哪个queue、记录下来便于归还资源
     */
    private String queueName;

    /**
     * 底层运行在哪个queue上
     */
    private String queueInYarn;

    /**
     * 实例生成的操作人:默认是scheduler
     * 重跑时会记录操作的用户
     */
    private String operator;

    /**
     * 实例生成的操作原因:默认是scheduler 自动例行
     * 重跑时会记录用户的操作原因
     */
    private String operationReason;

    /**
     * todo 加入表中、执行时本地进程id
     */
    private Integer processId;

    /**
     * todo 加入表中、执行时可能采集到的yarnId、用逗号分割
     */
    private String yarnIds;

    /**
     * 记录生成时间，实际上也就是 实例化开始时间
     */
    private Date ctime;

    /**
     * 记录最后更改时间
     */
    private Date mtime;
}
