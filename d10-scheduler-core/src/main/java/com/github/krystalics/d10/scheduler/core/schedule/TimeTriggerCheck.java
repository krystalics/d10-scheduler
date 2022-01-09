package com.github.krystalics.d10.scheduler.core.schedule;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.constant.Pair;
import com.github.krystalics.d10.scheduler.common.utils.SpringUtils;
import com.github.krystalics.d10.scheduler.core.pool.ScheduleExecutors;
import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author linjiabao001
 * @date 2022/1/9
 * @description 时间触发的任务检测
 */
public class TimeTriggerCheck implements ScheduledCheck {
    private static Logger log = LoggerFactory.getLogger(TimeTriggerCheck.class);
    private static TimeTriggerCheck instance = new TimeTriggerCheck();

    public static TimeTriggerCheck getInstance() {
        return instance;
    }

    //轮询的间隔，1min
    public static final long POLLING_INTER = 60000;
    private Thread scheduleThread;
    private volatile boolean scheduleThreadToStop = false;

    private static SchedulerMapper schedulerMapper = SpringUtils.getBean(SchedulerMapper.class);
    private static JobInstance jobInstance = SpringUtils.getBean(JobInstance.class);

    /**
     * 1。将本调度器分片范围的可运行任务取出
     * 2。设置为pending状态
     */
    @Override
    public void start() {
        scheduleThreadToStop = false;
        // schedule thread
        scheduleThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    TimeUnit.MILLISECONDS.sleep(5000 - System.currentTimeMillis() % 1000);
                } catch (InterruptedException e) {
                    if (!scheduleThreadToStop) {
                        log.error(e.getMessage(), e);
                    }
                }
                log.info(">>>>>>>>> init d10-scheduler admin scheduler success.");
                while (!scheduleThreadToStop) {
                    final Pair<Long, Long> taskIds = jobInstance.getTaskIds();
                    log.info("this scheduler's scope is {}", taskIds.toString());
                    // Scan Job
                    long start = System.currentTimeMillis();

                    try {
                        List<Long> scheduleList = schedulerMapper.schedulerReadVersionInstance(taskIds.getL(), taskIds.getR());
                        if (scheduleList != null && scheduleList.size() > 0) {
                            log.info("get {} version instances to schedule", scheduleList.size());
                            final List<List<Long>> partitions = Lists.partition(scheduleList, (scheduleList.size() / CommonConstants.INSTANCE_PER_PARTITION) + 1);

                            partitions.forEach(list->{
                                ScheduleExecutors.submitTimeCheck(()->{
                                    //todo update the state to waiting resource
                                });
                            });

                        } else {
                            log.warn("nothing to schedule");
                        }

                    } catch (Exception e) {
                        if (!scheduleThreadToStop) {
                            log.error(">>>>>>>>>>> d10-scheduler, JobScheduleHelper#scheduleThread error:{}", e);
                        }
                    }

                    long cost = System.currentTimeMillis() - start;
                    /**
                     * 如果上述过程消耗超过了1min、则直接进入下一轮
                     * 如果不足1min、则需要等待一会儿
                     */
                    if (cost < POLLING_INTER) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(POLLING_INTER - cost);
                        } catch (InterruptedException e) {
                            if (!scheduleThreadToStop) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }

                }

                log.info(">>>>>>>>>>> d10-scheduler, JobScheduleHelper#scheduleThread stop");
            }
        });
        scheduleThread.setDaemon(true);
        scheduleThread.setName("d10-scheduler");
        scheduleThread.start();
    }

    @Override
    public void stop() {
        if (scheduleThread != null) {
            scheduleThreadToStop = true;
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            if (scheduleThread.getState() != Thread.State.TERMINATED) {
                // interrupt and wait
                scheduleThread.interrupt();
                try {
                    scheduleThread.join();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }

            log.warn(">>>>>>>>>>> d10-scheduler, JobScheduleHelper stop");
        }
    }

}
