package com.github.krystalics.d10.scheduler.start;

import com.github.krystalics.d10.scheduler.resource.manager.common.ResourceConstants;
import com.github.krystalics.d10.scheduler.resource.manager.service.LockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author linjiabao001
 * @Date 2022/1/18
 * @Description
 */
public class LockTest extends BaseTest{
    @Autowired
    private LockService lockService;

    @Test
    public void lock() throws InterruptedException {
        lockService.lock(ResourceConstants.LOCK_PREFIX + "test");
        Thread.sleep(10000);
        lockService.unlock(ResourceConstants.LOCK_PREFIX + "test");
    }
}
