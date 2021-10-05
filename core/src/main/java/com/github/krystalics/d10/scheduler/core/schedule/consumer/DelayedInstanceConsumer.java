//package com.github.krystalics.d10.scheduler.core.schedule.consumer;
//
//import com.github.krystalics.d10.scheduler.core.common.VersionState;
//import com.github.krystalics.d10.scheduler.core.utils.SpringUtils;
//import com.github.krystalics.d10.scheduler.dao.entity.Instance;
//import com.github.krystalics.d10.scheduler.core.schedule.delay.DelayedInstance;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * @author linjiabao001
// * @date 2020/5/4
// * @description
// */
//public class DelayedInstanceConsumer implements Runnable {
//    private static final Logger logger = LoggerFactory.getLogger(DelayedInstanceConsumer.class);
//
//    private SchedulerCenter schedulerCenter = SpringUtils.getBean(SchedulerCenter.class);
//
//    private AtomicInteger numberOfConsumedElements = new AtomicInteger();
//
//    /**
//     * 从延迟队列中将实例放入待分发队列
//     */
//    @Override
//    public void run() {
//        while (true) {
//            DelayedInstance delayedInstance = QueueService.takeFromTimeTriggerQueue();
//            numberOfConsumedElements.incrementAndGet();
//            QueueService.putIntoScheduledQueue(delayedInstance);
//
//            Instance instance = delayedInstance.getInstance();
//            if (instance.getState() < VersionState.SCHEDULING.getState()) {
//                instance.setState(VersionState.SCHEDULING.getState());
//                schedulerCenter.updateVersionAndInstance(instance);
//            }
//
//            logger.info("put to scheduled queue {}", instance);
//        }
//    }
//
//    public AtomicInteger getNumberOfConsumedElements() {
//        return numberOfConsumedElements;
//    }
//
//    public void setNumberOfConsumedElements(AtomicInteger numberOfConsumedElements) {
//        this.numberOfConsumedElements = numberOfConsumedElements;
//    }
//}
