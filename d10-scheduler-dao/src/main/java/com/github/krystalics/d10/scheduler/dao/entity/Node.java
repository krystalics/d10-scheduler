package com.github.krystalics.d10.scheduler.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author linjiabao001
 * @date 2022/2/3
 * @description
 */
@Data
public class Node implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String nodeAddress;
    private Double cpuUse;
    private Double cpuCapacity;
    private Double memoryUse;
    private Double memoryCapacity;
    private Double avgLoad1;
    private Double avgLoad5;
    private Double avgLoad15;
    private Boolean alive;
    private Date ctime;
    private Date mtime;
}
