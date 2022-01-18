package com.github.krystalics.d10.scheduler.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author linjiabao001
 * @date 2022/1/18
 * @description
 */
@Data
public class Tenant implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantCode;
    private String description;
    private Long queueId;
    private Date ctime;
    private Date mtime;
}
