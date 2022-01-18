package com.github.krystalics.d10.scheduler.dao.mapper;

import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.Queue;
import com.github.krystalics.d10.scheduler.dao.qm.QueueQM;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linjiabao001
 * @date 2022/1/18
 * @description
 */
@Mapper
public interface QueueMapper extends BaseMapper<Queue, QueueQM> {

}
