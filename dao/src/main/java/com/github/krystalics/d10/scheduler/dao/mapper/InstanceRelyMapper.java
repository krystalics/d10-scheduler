package com.github.krystalics.d10.scheduler.dao.mapper;


import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.InstanceRely;
import com.github.krystalics.d10.scheduler.dao.qm.InstanceRelyQM;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */
@Mapper
public interface InstanceRelyMapper extends BaseMapper<InstanceRely, InstanceRelyQM> {

}
