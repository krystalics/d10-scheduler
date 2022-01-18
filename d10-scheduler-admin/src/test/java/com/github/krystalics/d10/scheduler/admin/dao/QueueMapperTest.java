package com.github.krystalics.d10.scheduler.admin.dao;

import com.github.krystalics.d10.scheduler.admin.BaseTest;
import com.github.krystalics.d10.scheduler.dao.entity.Queue;
import com.github.krystalics.d10.scheduler.dao.mapper.QueueMapper;
import com.github.krystalics.d10.scheduler.dao.qm.QueueQM;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author linjiabao001
 * @date 2022/1/18
 * @description
 */
public class QueueMapperTest extends BaseTest {

    @Autowired
    private QueueMapper queueMapper;

    @Test
    public void list() {
        QueueQM queueQM = new QueueQM();
        System.out.println(queueMapper.list(queueQM));
    }

    @Test
    public void insert() {
        Queue queue=new Queue();
        queue.setQueueName("test");
        queue.setQueueInYarn("hadoop-test");
        queue.setPriority(1);
        queueMapper.insert(queue);
        System.out.println(queue);
    }
}
