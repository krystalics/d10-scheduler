package com.github.krystalics.d10.scheduler.dao.qm;

import com.github.krystalics.d10.scheduler.dao.module.BaseQM;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VersionQM extends BaseQM {
    private Long versionId;
    private Long taskId;
    private String versionNo;
}
