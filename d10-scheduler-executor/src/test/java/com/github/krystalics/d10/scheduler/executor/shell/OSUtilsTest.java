package com.github.krystalics.d10.scheduler.executor.shell;

import com.github.krystalics.d10.scheduler.executor.common.Constants;
import com.github.krystalics.d10.scheduler.executor.utils.OSUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author linjiabao001
 * @Date 2022/2/8
 * @Description
 */
public class OSUtilsTest {
    @Test
    public void process() throws IOException {
        String processName = "Executor";
        String[] split = OSUtils.getProcessIds(processName);
        for (String s : split) {
            System.out.println(s);
        }

        OSUtils.killProcesses(split);

        String[] split2 = OSUtils.getProcessIds(processName);
        for (String s : split2) {
            System.out.println(s);
        }
    }
}
