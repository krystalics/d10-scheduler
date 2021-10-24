//package com.github.krystalics.d10.scheduler.core.schedule.worker;
//
//
//import com.github.krystalics.d10.scheduler.core.utils.SpringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @author linjiabao001
// * @date 2020/5/10
// * @description
// */
//public class StartWorker implements Runnable {
//    private static final Logger logger = LoggerFactory.getLogger("INIT");
//
//    @Override
//    public void run() {
//        try {
//            Thread.sleep(2000); //这里引用到了scheduleCenter，没有sleep的话会得到一个null初始化错误
//            schedulerCenter.initVersionsAndInstance();
//        } catch (InterruptedException e) {
//            logger.error("init error!!!! retry");
//            try {
//                schedulerCenter.initVersionsAndInstance();
//            } catch (InterruptedException ex) {
//                logger.error("init error!!!! already retry 1 time , init failed");
//            }
//        }
//    }
//}
