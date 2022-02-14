package com.github.krystalics.d10.scheduler.common.constant;

import lombok.Data;

import java.io.Serializable;

/**
 * @author linjiabao001
 * @date 2021/10/19
 * @description 表示的是集群中的运行节点的信息
 */
@Data
public class JobInstance implements Comparable<JobInstance>, Serializable {
    private static final long serialVersionUID = 1L;


    private String address;
    /**
     * 负责的任务范围
     */
    private Pair<Long, Long> taskIds;

    /**
     * 按照地址进行排序
     */
    @Override
    public int compareTo(JobInstance instance) {
        return this.address.compareTo(instance.getAddress());
    }
}
