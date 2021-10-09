package com.github.krystalics.d10.scheduler.core.schedule;


import com.github.krystalics.d10.scheduler.common.common.FrequencyGranularity;
import com.github.krystalics.d10.scheduler.core.common.Constant;
import com.github.krystalics.d10.scheduler.core.utils.CronUtils;
import com.github.krystalics.d10.scheduler.core.utils.DateUtils;
import com.github.krystalics.d10.scheduler.dao.entity.Instance;
import com.github.krystalics.d10.scheduler.dao.entity.Task;
import com.github.krystalics.d10.scheduler.dao.entity.Version;
import com.github.krystalics.d10.scheduler.dao.mapper.InstanceMapper;
import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import com.github.krystalics.d10.scheduler.dao.mapper.VersionMapper;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

/**
 * @author linjiabao001
 * @date 2021/10/8
 * @description
 */
@Component
@Slf4j
public class DistributedScheduler {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private VersionMapper versionMapper;

    @Autowired
    private InstanceMapper instanceMapper;

    private CountDownLatch latch;

    public void init() throws InterruptedException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(Constant.SYSTEM_TIME_ZONE));
        log.info("scheduler start init the tasks, now is {}", now);
        TaskQM taskQM = new TaskQM();
        taskQM.setState(2);


        List<Task> tasks = taskMapper.list(taskQM);
        latch = new CountDownLatch(tasks.size());

        for (Task task : tasks) {
//            tasksMap.put(task.getTaskId(), task);
//            InitExecutors.submit(new InitWorker(task));
        }

        latch.await();
        log.info("init versions and instance finished , cost " + ((System.currentTimeMillis() / 1000) - now.toEpochSecond()) + " s");


    }

    public void initTask(Task task) {
        Date todayMaxTime = DateTime.now().millisOfDay().withMaximumValue().toDate();
        Date date = DateTime.now().minusDays(1).millisOfDay().withMaximumValue().toDate();


        try {
            //小时级任务，需要生成24个版本
            if (FrequencyGranularity.HOUR.getValue() == task.getFrequency()) {
                for (int i = 0; i < 24; i++) {
                    date = initVersionAndInstance(date, todayMaxTime);
                }
            } else {
                initVersionAndInstance(date, todayMaxTime);
            }
        } catch (Exception e) {
            log.error("init task error where task id = " + task.getTaskId() + " and task name = " + task.getTaskName(), e);
            return;
        } finally {
            latch.countDown();
        }
//        log.info("init task where task id =" + task.getTaskId() + " and task name = " + task.getTaskName() + " success!!!");

    }

    private Date initVersionAndInstance(Task task, ZonedDateTime date, ZonedDateTime todayMaxTime) {
        date = CronUtils.nextExecutionDate(date, task.getCrontab());
        if (date.isAfter(todayMaxTime)) {
            return null;
        }
        String versionNo = DateUtils.versionNo(date, task.getFrequency());
        Instance instance = schedulerCenter.insertVersionAndInstance(task, versionNo, date);
        putIntoQueue(instance);
        return date;
    }

    public Instance insertVersionAndInstance(Task task, String versionNo, Date startTimeTheory) {
        Version version = versionMapper.findVersionByTaskIdAndVersionNo(task.getTaskId(), versionNo);
        if (version == null) {
            version = generateVersion(task, versionNo);
            versionMapper.insertVersion(version);
        }
        int versionId = version.getVersionId();

        Instance instance = instanceMapper.findLastInstanceByVersionId(versionId);
        if (instance == null) {
            instance = generateInstance(task, versionId, startTimeTheory);
            instanceMapper.insertInstance(instance);
        }
        int instanceId = instance.getInstanceId();

        version.setLastInstanceId(instanceId);
        versionMapper.updateLastInstanceId(instanceId, versionId);

        versionsMap.put(versionId, version);
        instancesMap.put(instanceId, instance);

        CopyOnWriteArraySet<Version> versions = taskIdAndVersionsMap.getOrDefault(task.getTaskId(), new CopyOnWriteArraySet<Version>());
        versions.add(version);
        taskIdAndVersionsMap.put(task.getTaskId(), versions);

        return instance;
    }


    /**
     * 先查看数据库中有没有该版本，用于服务重启时
     *
     * @param task
     * @param versionNo
     * @return
     */
    private Version generateVersion(Task task, String versionNo) {
        Version version = new Version();
        version.setTaskId(task.getTaskId());
        version.setRetryRemainTimes(task.getRetryTimes());
        version.setVersionNo(versionNo);
        return version;
    }

    /**
     * 先查看数据库中有没有该实例，用于服务重启时
     */
    public Instance generateInstance(Task task, long versionId, Date startTimeTheory) {
        Instance instance = new Instance();
        instance.setJobConf(task.getJobConf());
        instance.setState(1);
        instance.setTaskId(task.getTaskId());
        instance.setVersionId(versionId);
        instance.setStartTimeTheory(startTimeTheory);
        return instance;
    }
}
