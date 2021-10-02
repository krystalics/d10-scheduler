package com.github.krystalics.d10.scheduler.dao.entity;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据版本表
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */
public class Version implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据版本id
     */
    private Long versionId;

    /**
     * 版本号
     */
    private String versionNo;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 这个数据版本对应的最新一次的实例id
     */
    private Long lastInstanceId;

    /**
     * 任务重试剩余次数
     */
    private Integer retryRemainTimes;

    /**
     * 实例的状态: 1新建，2:等待 3:已分发 4:运行中 5:成功 6:失败 7:杀死
     */
    private Integer state;

    private String upRelies;

    /**
     * 记录生成时间
     */
    private LocalDateTime ctime;

    /**
     * 记录最后更改时间
     */
    private LocalDateTime mtime;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }
    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    public Long getLastInstanceId() {
        return lastInstanceId;
    }

    public void setLastInstanceId(Long lastInstanceId) {
        this.lastInstanceId = lastInstanceId;
    }
    public Integer getRetryRemainTimes() {
        return retryRemainTimes;
    }

    public void setRetryRemainTimes(Integer retryRemainTimes) {
        this.retryRemainTimes = retryRemainTimes;
    }
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    public String getUpRelies() {
        return upRelies;
    }

    public void setUpRelies(String upRelies) {
        this.upRelies = upRelies;
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
        return "Version{" +
            "versionId=" + versionId +
            ", versionNo=" + versionNo +
            ", taskId=" + taskId +
            ", lastInstanceId=" + lastInstanceId +
            ", retryRemainTimes=" + retryRemainTimes +
            ", state=" + state +
            ", upRelies=" + upRelies +
            ", ctime=" + ctime +
            ", mtime=" + mtime +
        "}";
    }
}
