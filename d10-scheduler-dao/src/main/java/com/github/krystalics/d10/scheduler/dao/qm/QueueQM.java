package com.github.krystalics.d10.scheduler.dao.qm;

import com.github.krystalics.d10.scheduler.dao.module.BaseQM;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author linjiabao001
 * @date 2022/1/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueueQM extends BaseQM {
    private String queueName;
}