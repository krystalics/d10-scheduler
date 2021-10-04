package com.github.krystalics.d10.scheduler.dao.mapper;


import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.Instance;
import com.github.krystalics.d10.scheduler.dao.qm.InstanceQM;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 实例，任务实例 Mapper 接口
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */
@Mapper
public interface InstanceMapper extends BaseMapper<Instance, InstanceQM> {

}
