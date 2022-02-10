package com.github.krystalics.d10.scheduler.core.schedule.check;

import com.github.krystalics.d10.scheduler.common.constant.ScheduledEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * xxl-job 在后续的版本中脱离了quartz体系，所以这里直接借鉴它的做法。创建项目自己的轮询体系
 */
public class SchedulerCheckHelper implements ScheduledCheck {
    private static Logger log = LoggerFactory.getLogger(SchedulerCheckHelper.class);

    public SchedulerCheckHelper(ScheduledCheck scheduledCheck, long polling, ScheduledEnum scheduledEnum) {
        this.scheduledCheck = scheduledCheck;
        this.POLLING_INTER = polling;
        this.scheduledEnum = scheduledEnum;
        this.scheduledName = scheduledEnum.getDesc();
    }

    //轮询的间隔
    public long POLLING_INTER;
    private Thread scheduleThread;
    private volatile boolean scheduleThreadToStop = false;
    private final ScheduledCheck scheduledCheck;
    private final String scheduledName;
    private final ScheduledEnum scheduledEnum;


    /**
     * 1。将本调度器分片范围的可运行任务取出
     * 2。进行资源位的竞争、为多租户设计留下口子
     * 3。获得资源位的可以进入分发队列、没有的则跳过
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
                log.info(">>>>>>>>>d10-scheduler {} start.", scheduledName);
                while (!scheduleThreadToStop) {

                    long start = System.currentTimeMillis();

                    try {
                        scheduledCheck.start();
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

                log.info(">>>>>>>>>>> d10-scheduler, {} stop", scheduledName);
            }
        });
        scheduleThread.setDaemon(true);
        scheduleThread.setName(scheduledName);
        log.info(scheduledName + " thread start");
        scheduleThread.start();
    }

    @Override
    public void stop() {
        if (scheduleThread != null) {
            scheduleThreadToStop = true;
            try {
                scheduledCheck.stop();
                TimeUnit.SECONDS.sleep(10);
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

    @Override
    public boolean checkStop() {
        if (scheduleThread == null) {
            return true;
        }
        return scheduleThread.getState() == Thread.State.TERMINATED;
    }

}
