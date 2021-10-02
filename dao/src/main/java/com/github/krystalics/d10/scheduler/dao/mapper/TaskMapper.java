package com.github.krystalics.d10.scheduler.dao.mapper;

import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.Task;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linjiabao001
 * @date 2021/10/2
 * @description
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task, TaskQM> {

}
