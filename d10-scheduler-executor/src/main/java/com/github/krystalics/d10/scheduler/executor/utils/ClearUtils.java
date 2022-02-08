package com.github.krystalics.d10.scheduler.executor.utils;

import com.github.krystalics.d10.scheduler.executor.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author linjiabao001
 * @Date 2022/2/8
 * @Description 扫描出所有的子进程进行kill
 */
public class ClearUtils {
    private static Logger log = LoggerFactory.getLogger(ClearUtils.class);


    public static void killAllSubProcesses() throws IOException {
        String[] processIds = OSUtils.getProcessIds(Constants.SUB_PROCESS_PREFIX);
        log.info("clear the node before start! the processId is {}", Arrays.toString(processIds));
        OSUtils.killProcesses(processIds);
    }
}
