package com.github.krystalics.d10.scheduler.executor.utils;

import com.github.krystalics.d10.scheduler.executor.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Author linjiabao001
 * @Date 2022/2/8
 * @Description 扫描出所有的子进程进行kill
 */
public class ClearUtils {
    private static final Logger log = LoggerFactory.getLogger(ClearUtils.class);

    private static final String KILL_YARN_APPID_CMD_PREFIX = "yarn application -kill ";

    public static void killAllSubProcesses() throws IOException {
        String[] processIds = OSUtils.getProcessIds(Constants.SUB_PROCESS_PREFIX);
        log.info("clear the node before start! the processId is {}", Arrays.toString(processIds));
        OSUtils.killProcesses(processIds);
    }

    /**
     * todo 初期只支持杀yarn的，后期其他的类型等待拓展
     *
     * @param id id
     */
    public static void killRemoteApp(String id) throws IOException {
        if (StringUtils.isEmpty(id)) {
            return;
        }
        log.info("need to kill remote application {} before start!", id);
        String command = KILL_YARN_APPID_CMD_PREFIX + id;
        OSUtils.exeCmd(command);
    }
}
