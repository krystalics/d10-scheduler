package com.github.krystalics.d10.scheduler.dao.mapper;

import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.Queue;
import com.github.krystalics.d10.scheduler.dao.qm.QueueQM;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author linjiabao001
 * @date 2022/1/18
 * @description
 */
@Mapper
public interface QueueMapper extends BaseMapper<Queue, QueueQM> {
    /**
     * 通过队列名获取该队列的所有信息
     * @param queueName 队列名
     * @return 该队列的所有信息
     */
    Queue getByName(@Param("queueName") String queueName);
}
