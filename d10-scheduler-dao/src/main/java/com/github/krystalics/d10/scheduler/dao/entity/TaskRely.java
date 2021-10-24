package com.github.krystalics.d10.scheduler.dao.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 任务之间依赖关系配置表
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */
@Data
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

}
