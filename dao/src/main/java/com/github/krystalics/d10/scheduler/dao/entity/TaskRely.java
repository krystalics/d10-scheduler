package com.github.krystalics.d10.scheduler.dao.entity;


import java.io.Serializable;

/**
 * <p>
 * 任务之间依赖关系配置表
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */

public class TaskRely implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */

    private Long id;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 上游游任务id
     */
    private Long upTaskId;

    /**
     * 偏移量。如果下游任务今天的运行数据 依赖上游任务前天的运行数据，则偏移量就是-2
     */
    private Integer offset;

    /**
     * 依赖的版本数量
     */
    private Integer cnt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getUpTaskId() {
        return upTaskId;
    }

    public void setUpTaskId(Long upTaskId) {
        this.upTaskId = upTaskId;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    @Override
    public String toString() {
        return "TaskRely{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", upTaskId=" + upTaskId +
                ", offset=" + offset +
                ", cnt=" + cnt +
                "}";
    }
}
