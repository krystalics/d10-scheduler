package com.github.krystalics.d10.scheduler.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
@Data
public class Version implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long versionId;
    private String versionNo;
    private Long taskId;
    private Long lastInstanceId;
    private Integer retryRemainTimes;
    private LocalDateTime ctime;
    private LocalDateTime mtime;
}
