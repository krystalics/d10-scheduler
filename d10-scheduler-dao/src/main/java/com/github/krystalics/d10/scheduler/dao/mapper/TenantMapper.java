package com.github.krystalics.d10.scheduler.dao.mapper;

import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.Tenant;
import com.github.krystalics.d10.scheduler.dao.qm.TenantQM;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linjiabao001
 * @date 2022/1/18
 * @description
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant, TenantQM> {

}
