package com.github.krystalics.d10.scheduler.admin.dao;

import com.github.krystalics.d10.scheduler.dao.mapper.TaskRelyMapper;
import com.github.krystalics.d10.scheduler.dao.qm.TaskRelyQM;
import com.github.krystalics.d10.scheduler.admin.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
public class TaskRelyTest extends BaseTest {
    @Autowired
    private TaskRelyMapper relyMapper;

    @Test
    public void list() {
        TaskRelyQM qm = new TaskRelyQM();
        qm.setTaskId(5L);

        System.out.println(relyMapper.list(qm));
    }
}
