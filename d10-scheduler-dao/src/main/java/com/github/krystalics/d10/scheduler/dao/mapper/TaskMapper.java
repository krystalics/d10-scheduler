package com.github.krystalics.d10.scheduler.dao.mapper;

import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.Task;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    /**
     * 默认情况下，mybatis 的 update 操作的返回值是 matched 的记录数，并不是受影响的记录数。
     * jdbc:mysql://localhost:3306/scheduler?useAffectedRows=true
     *
     * 更新成功、说明并发度占用成功；失败说明已经没位子了。避免先行锁再更新
     * @param taskId 要进行并发度占用的任务id
     * @return 返回0 代码更新失败；1代码更新成功
     */
    int updateTaskConcurrentOccupation(@Param("taskId") long taskId);
}
