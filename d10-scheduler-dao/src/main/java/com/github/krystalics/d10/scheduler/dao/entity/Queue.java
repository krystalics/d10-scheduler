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
public class Queue implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String queueName;
    private String queueInYarn;
    private Double cpuInUse;
    private Double cpuMax;
    private Double cpuMin;
    private Double cpuCapacity;
    private Double memoryInUse;
    private Double memoryMax;
    private Double memoryMin;
    private Double memoryCapacity;
    private Integer priority;

    private Date ctime;
    private Date mtime;
}
