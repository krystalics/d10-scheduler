//package com.github.krystalics.d10.scheduler.core.schedule.worker;
//
//
//import com.github.krystalics.d10.scheduler.core.common.Constant;
//import com.github.krystalics.d10.scheduler.common.common.FrequencyGranularity;
//import com.github.krystalics.d10.scheduler.common.common.VersionState;
//import com.github.krystalics.d10.scheduler.core.utils.CronUtils;
//import com.github.krystalics.d10.scheduler.core.utils.DateUtils;
//import com.github.krystalics.d10.scheduler.core.utils.SpringUtils;
//import com.github.krystalics.d10.scheduler.dao.entity.Instance;
//import com.github.krystalics.d10.scheduler.dao.entity.Task;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//
///**
// * @author linjiabao001
// * @date 2020/4/27
// * @description 将插入tb_instance表中的instance实例，放入等待触发的队列中
// */
//public class InitWorker implements Runnable {
//
//    private static final Logger logger = LoggerFactory.getLogger("INIT");
//    private final SchedulerCenter schedulerCenter = SpringUtils.getBean(SchedulerCenter.class);
//
//    private final Task task;
//
//    public InitWorker(Task task) {
//        this.task = task;
//    }
//
//    @Override
//    public void run() {
//
//        ZonedDateTime todayMaxTime = ZonedDateTime.of(LocalDateTime.MAX, ZoneId.of(Constant.SYSTEM_TIME_ZONE));
//        ZonedDateTime date = ZonedDateTime.of(LocalDateTime.MIN, ZoneId.of(Constant.SYSTEM_TIME_ZONE));
//
//        try {
//            //小时级任务，需要生成24个版本
//            if (FrequencyGranularity.HOUR.getValue() == task.getFrequency()) {
//                for (int i = 0; i < 24; i++) {
//                    date = initVersionAndInstance(date, todayMaxTime);
//                }
//            } else {
//                initVersionAndInstance(date, todayMaxTime);
//            }
//        } catch (Exception e) {
//            logger.error("init task error where task id = " + task.getTaskId() + " and task name = " + task.getTaskName(), e);
//            return;
//        } finally {
//            schedulerCenter.countDown();
//        }
//        logger.info("init task where task id =" + task.getTaskId() + " and task name = " + task.getTaskName() + " success!!!");
//
//    }
//
//    private ZonedDateTime initVersionAndInstance(ZonedDateTime date, ZonedDateTime todayMaxTime) {
//        date = CronUtils.nextExecutionDate(date, task.getCrontab());
//        if (date.isAfter(todayMaxTime)) {
//            return null;
//        }
//        String versionNo = DateUtils.versionNo(date,task.getFrequency());
//        Instance instance = schedulerCenter.insertVersionAndInstance(task, versionNo, date);
//        putIntoQueue(instance);
//        return date;
//    }
//
//    /**
//     * 0 : 时间触发
//     * 1 : 依赖触发
//     * 重启服务器时需要对成功和杀死的实例进行过滤，对于失败的实例 需要在分发的时候去判断它是否还有重试的机会
//     *
//     * @param instance
//     */
//    private void putIntoQueue(Instance instance) {
//        if (instance.getState() == VersionState.SUCCESS.getState() || instance.getState() == VersionState.KILLED.getState()) {
//            return;
//        }
//        if (instance.getState() == VersionState.INIT.getState()) {
//            instance.setState(VersionState.WAITING.getState());
//            schedulerCenter.updateVersionAndInstance(instance);
//        }
////        if (instance.getScheduleType() == ScheduleType.TIME.getScheduleType()) {
////            QueueService.putIntoTimeTriggerQueue(instance, task.getPriority());
////        }
//    }
//}
