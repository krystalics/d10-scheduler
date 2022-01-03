package com.github.krystalics.d10.scheduler.admin.dao;


import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import com.github.krystalics.d10.scheduler.admin.SpringBootBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskMapperTest extends SpringBootBaseTest {
    @Autowired
    private TaskMapper taskMapper;

    @Test
    public void findById(){
        System.out.println(taskMapper.getById(1));
    }

    @Test
    public void list(){
        TaskQM taskQM=new TaskQM();
        taskQM.setTaskName("stg");
        System.out.println(taskMapper.list(taskQM).size());
    }

    @Test
    public void count(){
        TaskQM taskQM=new TaskQM();
        taskQM.setTaskName("stg");
        System.out.println(taskMapper.count(taskQM));
    }
}
