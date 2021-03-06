package com.github.krystalics.d10.scheduler.admin.dao;

import com.github.krystalics.d10.scheduler.admin.BaseTest;
import com.github.krystalics.d10.scheduler.dao.mapper.InstanceMapper;
import com.github.krystalics.d10.scheduler.dao.qm.InstanceQM;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
public class InstacneMapperTest extends BaseTest {
    @Autowired
    private InstanceMapper versionInstanceMapper;

    @Test
    public void list() {
        InstanceQM qm = new InstanceQM();
        qm.setTaskId(1L);
        System.out.println(versionInstanceMapper.list(qm).size());
    }
}
