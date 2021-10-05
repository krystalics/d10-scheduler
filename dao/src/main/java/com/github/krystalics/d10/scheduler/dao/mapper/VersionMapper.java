package com.github.krystalics.d10.scheduler.dao.mapper;

import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.Version;
import com.github.krystalics.d10.scheduler.dao.qm.VersionQM;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
@Mapper
public interface VersionMapper extends BaseMapper<Version, VersionQM> {
}
