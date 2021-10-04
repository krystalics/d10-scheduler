package com.github.krystalics.d10.scheduler.dao.mapper;

import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.Task;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 任务表 Mapper 接口
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task, TaskQM> {

}
