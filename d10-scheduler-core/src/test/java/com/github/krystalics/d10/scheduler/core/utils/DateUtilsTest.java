package com.github.krystalics.d10.scheduler.core.utils;

import com.github.krystalics.d10.scheduler.common.utils.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author linjiabao001
 * @date 2021/10/2
 * @description
 */
public class DateUtilsTest {
    @Test
    public void date() {
        final ZonedDateTime dateTime = DateUtils.versionDateTime("20211002000000");
        Assertions.assertEquals("2021-10-02T00:00+08:00[Asia/Shanghai]", dateTime.toString());
    }

    @Test
    public void versionNo() {
        final ZonedDateTime dateTime = ZonedDateTime.now();
        final String s = DateUtils.versionNo(dateTime, 1);
        Assertions.assertEquals("20211002160000", s);
    }

    @Test
    public void test() {
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime);
    }
}
