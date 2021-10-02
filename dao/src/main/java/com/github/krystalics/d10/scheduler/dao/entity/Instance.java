package com.github.krystalics.d10.scheduler.dao.entity;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 实例，任务实例
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */
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
    private Integer scheduleType;

    private Integer type;

    /**
     * 实例将被调起的 物理时间点,形如"20140802 10:30",根据crontab填入
     */
    private LocalDateTime startTimeTheory;

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
    private LocalDateTime runStartTime;

    /**
     * 实例运行结束时间
     */
    private LocalDateTime runEndTime;

    /**
     * 实例执行节点
     */
    private String node;

    private Integer autoStart;

    /**
     * 记录生成时间，实际上也就是 实例化开始时间
     */
    private LocalDateTime ctime;

    /**
     * 记录最后更改时间
     */
    private LocalDateTime mtime;

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Integer getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(Integer scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public LocalDateTime getStartTimeTheory() {
        return startTimeTheory;
    }

    public void setStartTimeTheory(LocalDateTime startTimeTheory) {
        this.startTimeTheory = startTimeTheory;
    }

    public String getJobConf() {
        return jobConf;
    }

    public void setJobConf(String jobConf) {
        this.jobConf = jobConf;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getJobLogAddress() {
        return jobLogAddress;
    }

    public void setJobLogAddress(String jobLogAddress) {
        this.jobLogAddress = jobLogAddress;
    }

    public LocalDateTime getRunStartTime() {
        return runStartTime;
    }

    public void setRunStartTime(LocalDateTime runStartTime) {
        this.runStartTime = runStartTime;
    }

    public LocalDateTime getRunEndTime() {
        return runEndTime;
    }

    public void setRunEndTime(LocalDateTime runEndTime) {
        this.runEndTime = runEndTime;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public Integer getAutoStart() {
        return autoStart;
    }

    public void setAutoStart(Integer autoStart) {
        this.autoStart = autoStart;
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
        return "Instance{" +
                "instanceId=" + instanceId +
                ", taskId=" + taskId +
                ", versionId=" + versionId +
                ", scheduleType=" + scheduleType +
                ", type=" + type +
                ", startTimeTheory=" + startTimeTheory +
                ", jobConf=" + jobConf +
                ", state=" + state +
                ", jobLogAddress=" + jobLogAddress +
                ", runStartTime=" + runStartTime +
                ", runEndTime=" + runEndTime +
                ", node=" + node +
                ", autoStart=" + autoStart +
                ", ctime=" + ctime +
                ", mtime=" + mtime +
                "}";
    }
}
