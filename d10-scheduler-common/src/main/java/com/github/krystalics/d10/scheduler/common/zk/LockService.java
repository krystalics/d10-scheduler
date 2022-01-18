package com.github.krystalics.d10.scheduler.common.zk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @Author linjiabao001
 * @Date 2022/1/18
 * @Description
 */
@Service
public class LockService {
    @Autowired
    private ZookeeperLockRegistry zookeeperLockRegistry;
    /**
     * @param lockPath 锁路径
     * @param timeout  超时时间
     */
    public boolean tryLock(String lockPath, long timeout) throws Exception {
        Lock lock = zookeeperLockRegistry.obtain(lockPath);
        return lock.tryLock(timeout, TimeUnit.SECONDS);
    }

    public void lock(String lockPath){
        Lock lock = zookeeperLockRegistry.obtain(lockPath);
        lock.lock();
    }

    public void unlock(String lockPath){
        Lock lock = zookeeperLockRegistry.obtain(lockPath);
        lock.unlock();
    }
}
