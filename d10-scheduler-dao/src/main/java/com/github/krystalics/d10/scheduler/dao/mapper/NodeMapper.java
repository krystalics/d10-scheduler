package com.github.krystalics.d10.scheduler.dao.mapper;

import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.Node;
import com.github.krystalics.d10.scheduler.dao.qm.NodeQM;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linjiabao001
 * @date 2022/2/3
 * @description
 */
@Mapper
public interface NodeMapper extends BaseMapper<Node, NodeQM> {
}
