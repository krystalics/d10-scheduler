//package com.github.krystalics.d10.scheduler.core.schedule.worker;
//
//
//import com.github.krystalics.d10.scheduler.core.utils.SpringUtils;
//import com.github.krystalics.d10.scheduler.dao.entity.Task;
//import com.github.krystalics.d10.scheduler.dao.entity.Version;
//import org.joda.time.DateTime;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Date;
//import java.util.Set;
//
///**
// * @author linjiabao001
// * @date 2020/4/19
// * @description to init the task table
// * <pre>
// *     根据scheduler节点在Master那边注册的id进行划分，每个scheduler初始化属于自己的任务
// *     当有新scheduler节点加进来时，Master会将任务的节点归属会进行一次调整，策略可以是根据节点性能进行任务数量的分配，也可以是均分，之后再详细写
// *     节点归属调整完之后，次日进行初始化时就会按照新的归属进行。当有用户需要重跑任务时，前端Nginx负载均衡到api ，api根据任务现有节点归属 将请求发送给该id的scheduler节点进行触发
// *
// *     涉及到分布式协调的分布式锁等问题在代码中详细阐述
// * </pre>
// * <p>
// * 1.根据scheduler节点编号获取任务信息，首先初始化当天的版本
// * 2.再初始化版本的实例 这两步需要往数据库插入数据
// * 3.在生成version instance对象时 ，拼接出需要触发的scheduled对象，放入队列
// * 分为三个部分同时进行，核心任务，高优任务，普通任务
// */
//public class RelyWorker implements Runnable {
//    private static final Logger logger = LoggerFactory.getLogger("INIT-RELY");
//
//    private Task task;
//
//    public RelyWorker(Task task) {
//        this.task = task;
//    }
//
//    SchedulerCenter schedulerCenter = SpringUtils.getBean(SchedulerCenter.class);
//    RelyService relyService = SpringUtils.getBean(RelyService.class);
//
//
//    @Override
//    public void run() {
//        Date date = DateTime.now().minusDays(1).millisOfDay().withMaximumValue().toDate();
//        int taskId = task.getTaskId();
//        Set<Version> versions = schedulerCenter.getVersionsByTaskId(taskId); //在初始化时，位于initWorker之后执行，initWorker会将其写入map中，所以直接从map中获取
//
//        relyService.initRelies(taskId, date, versions);
//
//        logger.info("version rely generated where task id is {} ", taskId);
//        schedulerCenter.countDown();
//    }
//
//
//}
