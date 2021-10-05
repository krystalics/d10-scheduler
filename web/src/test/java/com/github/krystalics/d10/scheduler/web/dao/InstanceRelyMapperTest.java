package com.github.krystalics.d10.scheduler.web.dao;

import com.github.krystalics.d10.scheduler.dao.mapper.InstanceRelyMapper;
import com.github.krystalics.d10.scheduler.dao.qm.InstanceRelyQM;
import com.github.krystalics.d10.scheduler.web.SpringBootBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
public class InstanceRelyMapperTest extends SpringBootBaseTest {
    @Autowired
    private InstanceRelyMapper instanceRelyMapper;

    @Test
    public void list(){
        System.out.println(instanceRelyMapper.list(new InstanceRelyQM()));
    }
}
