package com.github.krystalics.d10.scheduler.dao.entity;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */
@Data
public class InstanceRely implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long instanceId;

    private Long upTaskId;

    private String upVersionNo;

    private LocalDateTime ctime;
}
