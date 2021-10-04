package com.github.krystalics.d10.scheduler.dao.entity;


import lombok.Data;

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
@Data
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

}
