package com.github.krystalics.d10.scheduler.start;

import com.github.krystalics.d10.scheduler.common.utils.SpringUtils;
import com.github.krystalics.d10.scheduler.dao.biz.VersionInstance;
import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * xxl-job 在后续的版本中脱离了quartz体系，所以这里直接借鉴它的做法。创建项目自己的轮训体系
 * todo 调度器分片的结果这里要可见
 */

public class D10SchedulerHelper {
    private static Logger log = LoggerFactory.getLogger(D10SchedulerHelper.class);
    private static D10SchedulerHelper instance = new D10SchedulerHelper();

    public static D10SchedulerHelper getInstance() {
        return instance;
    }

    //轮询的间隔，1min
    public static final long POLLING_INTER = 60000;
    private Thread scheduleThread;
    private volatile boolean scheduleThreadToStop = false;

    private static SchedulerMapper schedulerMapper = SpringUtils.getBean(SchedulerMapper.class);

    /**
     * 1。将本调度器分片范围的可运行任务取出
     * 2。进行资源位的竞争、为多租户设计留下口子
     * 3。获得资源位的可以进入分发队列、没有的则跳过
     */
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

                    // Scan Job
                    long start = System.currentTimeMillis();

                    try {
                        List<VersionInstance> scheduleList = schedulerMapper.schedulerReadVersionInstance(1, 10000);
                        if (scheduleList != null && scheduleList.size() > 0) {
                            log.info("get {} version instances to schedule", scheduleList.size());
                            for (VersionInstance versionInstance : scheduleList) {

                            }

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
