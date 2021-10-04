package com.github.krystalics.d10.scheduler.dao.mapper;


import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.Version;
import com.github.krystalics.d10.scheduler.dao.qm.VersionQM;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 数据版本表 Mapper 接口
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */
@Mapper
public interface VersionMapper extends BaseMapper<Version, VersionQM> {

}
