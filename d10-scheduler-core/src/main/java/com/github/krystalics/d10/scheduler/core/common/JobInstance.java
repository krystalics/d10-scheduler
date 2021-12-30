package com.github.krystalics.d10.scheduler.core.common;

import com.github.krystalics.d10.scheduler.common.constant.Pair;
import lombok.Data;

/**
 * @author linjiabao001
 * @date 2021/10/19
 * @description 表示的是集群中的运行节点
 */
@Data
public class JobInstance {
    private String address;
    private boolean isLeader;
    /**
     * 负责的任务范围
     */
    private Pair<Long, Long> taskIds;
}
