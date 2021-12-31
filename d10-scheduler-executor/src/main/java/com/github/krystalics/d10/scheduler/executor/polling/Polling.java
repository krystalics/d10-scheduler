package com.github.krystalics.d10.scheduler.executor.polling;

/**
 * @Author linjiabao001
 * @Date 2021/12/31 18:27
 * @Description TODO
 */
public interface Polling {
    /**
     * 调起任务
     * @return 调起是否成功
     */
    boolean start();

    /**
     * 轮询任务
     * @return 运行是否成功
     */
    boolean check();

    /**
     * 杀死任务
     */
    void kill();
}
