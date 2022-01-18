package com.github.krystalics.d10.scheduler.admin.dao;

import com.github.krystalics.d10.scheduler.admin.BaseTest;
import com.github.krystalics.d10.scheduler.dao.entity.Tenant;
import com.github.krystalics.d10.scheduler.dao.mapper.TenantMapper;
import com.github.krystalics.d10.scheduler.dao.qm.TenantQM;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author linjiabao001
 * @date 2022/1/18
 * @description
 */
public class TenantMapperTest extends BaseTest {

    @Autowired
    private TenantMapper tenantMapper;

    @Test
    public void list() {
        TenantQM tenantQM = new TenantQM();
        System.out.println(tenantMapper.list(tenantQM));
    }

    @Test
    public void insert() {
        Tenant tenant = new Tenant();
        tenant.setTenantCode("test");
        tenant.setDescription("test");
        tenant.setQueueId(1L);
        tenantMapper.insert(tenant);
        System.out.println(tenant);
    }
}
