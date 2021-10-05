package com.github.krystalics.d10.scheduler.dao.qm;

import com.github.krystalics.d10.scheduler.dao.module.BaseQM;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author linjiabao001
 * @date 2021/10/2
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TaskQM extends BaseQM {
    private String taskName;
    private String ownerMailLike;
}