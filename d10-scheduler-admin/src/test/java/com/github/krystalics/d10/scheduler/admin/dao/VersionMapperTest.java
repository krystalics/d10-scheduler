package com.github.krystalics.d10.scheduler.admin.dao;

import com.github.krystalics.d10.scheduler.dao.mapper.VersionMapper;
import com.github.krystalics.d10.scheduler.dao.qm.VersionQM;
import com.github.krystalics.d10.scheduler.admin.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
public class VersionMapperTest extends BaseTest {
    @Autowired
    private VersionMapper versionMapper;

    @Test
    public void list(){
        versionMapper.list(new VersionQM());
    }
}
