package com.github.krystalics.d10.scheduler.admin.dao;

import com.github.krystalics.d10.scheduler.admin.BaseTest;
import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
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
    public void route(){
        final List<VersionInstance> versionInstances = schedulerMapper.routingSchedulingInstances(0, 10000,new Date());
        System.out.println(versionInstances.size());
//        versionInstances.forEach(System.out::println);
    }
}
