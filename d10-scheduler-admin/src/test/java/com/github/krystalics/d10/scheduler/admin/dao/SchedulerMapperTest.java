package com.github.krystalics.d10.scheduler.admin.dao;

import com.github.krystalics.d10.scheduler.dao.biz.VersionInstance;
import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
import com.github.krystalics.d10.scheduler.admin.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author linjiabao001
 * @date 2022/1/3
 * @description
 */
public class SchedulerMapperTest extends BaseTest {

    @Autowired
    private SchedulerMapper schedulerMapper;

    @Test
    public void read() {
        final List<VersionInstance> versionInstances = schedulerMapper.readCouldTimeTriggered(0, 10000);
        System.out.println(versionInstances.size());
    }
}
