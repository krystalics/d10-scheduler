package com.github.krystalics.d10.scheduler.dao.entity;


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
     * 默认被编号被该id的scheduler加载，索引。
     */
    private Integer schedulerNodeId;

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
     * 任务归属项目组id,即tb_team表的team_id
     */
    private Long teamId;

    /**
     * 任务归属人邮箱前缀
     */
    private String ownerMailPrefix;

    /**
     * 任务创建人邮箱前缀,如zhangjingyi
     */
    private String creatorMailPrefix;

    /**
     * 需求方
     */
    private String demandSideUser;

    /**
     * 任务失败重试次数
     */
    private Integer retryTimes;

    /**
     * 其他团队是否可见(默认可见,true。 不可见为false)
     */
    private Boolean isOtherSee;

    /**
     * 任务状态,1表示上线 0表示下线.
     */
    private Integer state;

    /**
     * 任务备注
     */
    private String note;

    /**
     * 任务标签
     */
    private String tag;

    /**
     * 任务初始优先级
     */
    private Integer priority;

    /**
     * 任务来源系统; 1调度系统，2api，3adhoc，4数据交换
     */
    private Integer fromSourceCode;

    /**
     * 任务平均的数据行数：用于Executor的负载计算
     */
    private Integer avgLineNumber;

    /**
     * 任务平均的运行时长：用于Executor的负载计算
     */
    private Double avgRunDuration;

    private LocalDateTime ctime;

    /**
     * 记录最后更改时间
     */
    private LocalDateTime mtime;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getSchedulerNodeId() {
        return schedulerNodeId;
    }

    public void setSchedulerNodeId(Integer schedulerNodeId) {
        this.schedulerNodeId = schedulerNodeId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(Integer scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Integer getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(Integer concurrency) {
        this.concurrency = concurrency;
    }

    public String getCrontab() {
        return crontab;
    }

    public void setCrontab(String crontab) {
        this.crontab = crontab;
    }

    public String getJobConf() {
        return jobConf;
    }

    public void setJobConf(String jobConf) {
        this.jobConf = jobConf;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getOwnerMailPrefix() {
        return ownerMailPrefix;
    }

    public void setOwnerMailPrefix(String ownerMailPrefix) {
        this.ownerMailPrefix = ownerMailPrefix;
    }

    public String getCreatorMailPrefix() {
        return creatorMailPrefix;
    }

    public void setCreatorMailPrefix(String creatorMailPrefix) {
        this.creatorMailPrefix = creatorMailPrefix;
    }

    public String getDemandSideUser() {
        return demandSideUser;
    }

    public void setDemandSideUser(String demandSideUser) {
        this.demandSideUser = demandSideUser;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Boolean getIsOtherSee() {
        return isOtherSee;
    }

    public void setIsOtherSee(Boolean isOtherSee) {
        this.isOtherSee = isOtherSee;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getFromSourceCode() {
        return fromSourceCode;
    }

    public void setFromSourceCode(Integer fromSourceCode) {
        this.fromSourceCode = fromSourceCode;
    }

    public Integer getAvgLineNumber() {
        return avgLineNumber;
    }

    public void setAvgLineNumber(Integer avgLineNumber) {
        this.avgLineNumber = avgLineNumber;
    }

    public Double getAvgRunDuration() {
        return avgRunDuration;
    }

    public void setAvgRunDuration(Double avgRunDuration) {
        this.avgRunDuration = avgRunDuration;
    }

    public LocalDateTime getCtime() {
        return ctime;
    }

    public void setCtime(LocalDateTime ctime) {
        this.ctime = ctime;
    }

    public LocalDateTime getMtime() {
        return mtime;
    }

    public void setMtime(LocalDateTime mtime) {
        this.mtime = mtime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName=" + taskName +
                ", schedulerNodeId=" + schedulerNodeId +
                ", type=" + type +
                ", frequency=" + frequency +
                ", scheduleType=" + scheduleType +
                ", concurrency=" + concurrency +
                ", crontab=" + crontab +
                ", jobConf=" + jobConf +
                ", teamId=" + teamId +
                ", ownerMailPrefix=" + ownerMailPrefix +
                ", creatorMailPrefix=" + creatorMailPrefix +
                ", demandSideUser=" + demandSideUser +
                ", retryTimes=" + retryTimes +
                ", isOtherSee=" + isOtherSee +
                ", state=" + state +
                ", note=" + note +
                ", tag=" + tag +
                ", priority=" + priority +
                ", fromSourceCode=" + fromSourceCode +
                ", avgLineNumber=" + avgLineNumber +
                ", avgRunDuration=" + avgRunDuration +
                ", ctime=" + ctime +
                ", mtime=" + mtime +
                "}";
    }
}
