package com.github.krystalics.d10.scheduler.executor.utils;

import com.github.krystalics.d10.scheduler.executor.common.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);

    public static Future<?>[] LogCollect(Process process, String errorPath, String outputPath, Boolean flag) {
        FileOutputStream outputStream = null;
        FileOutputStream errorStream = null;
        try {

            outputStream = new FileOutputStream(outputPath, flag);
            errorStream = new FileOutputStream(errorPath, flag);
            ExecutorService exec = Executors.newFixedThreadPool(2);

            Future<?> errorRedirectTask = exec
                    .submit(new StreamGobbler(process.getErrorStream(), "", errorStream));
            Future<?> outputRedirectTask = exec.submit(new StreamGobbler(process.getInputStream(), "",
                    outputStream));
            exec.shutdown();
            return new Future<?>[]{errorRedirectTask, outputRedirectTask};

        } catch (Throwable throwable) {
            if (outputStream != null) {
                IOUtils.closeQuietly(outputStream);
            }
            if (errorStream != null) {
                IOUtils.closeQuietly(errorStream);
            }
            String errMsg = String.format("runner %s fail to create log file", outputPath);
            LOGGER.warn(errMsg, throwable);
            throw new RuntimeException(errMsg, throwable);
        }
    }

    public static Future<?>[] LogCollectWithLimit(Process process, String errorPath, String outputPath, Boolean flag, int cntLimit) {
        FileOutputStream outputStream = null;
        FileOutputStream errorStream = null;
        try {

            outputStream = new FileOutputStream(outputPath, flag);
            errorStream = new FileOutputStream(errorPath, flag);
            ExecutorService exec = Executors.newFixedThreadPool(2);

            Future<?> errorRedirectTask = exec
                    .submit(new StreamGobblerWithLimit(process.getErrorStream(), "", errorStream, cntLimit));
            Future<?> outputRedirectTask = exec.submit(new StreamGobblerWithLimit(process.getInputStream(), "",
                    outputStream, cntLimit));
            exec.shutdown();
            return new Future<?>[]{errorRedirectTask, outputRedirectTask};

        } catch (Throwable throwable) {
            if (outputStream != null) {
                IOUtils.closeQuietly(outputStream);
            }
            if (errorStream != null) {
                IOUtils.closeQuietly(errorStream);
            }
            String errMsg = String.format("runner %s fail to create log file", outputPath);
            LOGGER.warn(errMsg, throwable);
            throw new RuntimeException(errMsg, throwable);
        }
    }

    /**
     * 创建目录
     *
     * @param filePath
     * @throws IOException
     */
    public static void mkdirs(String filePath) throws IOException {
        FileUtils.forceMkdir(new File(filePath));
    }

    /**
     * 根据executor类型，返回该executor的log目录路径
     *
     * @param type       任务类型
     * @param instanceId 实例id
     * @return 日志路径
     */
    public static String logFile(String type, long instanceId, Date date) {
        return logDir(type, date) + "/" + instanceId + ".html";
    }

    /**
     * 根据executor类型，返回该executor的log目录路径
     *
     * @param type 任务类型
     * @param date 日期
     * @return 日志路径
     */
    public static String logDir(String type, Date date) {
        String executeDate = DateFormatUtils.format(date, "yyyyMMdd");
        return Constants.LOG_FILE_ADDRESS_PREFIX + executeDate + "/" + type;
    }

    public static void appendLog(String path, String logInfo) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            logInfo = "[" + sdf.format(new Date()) + "] -- " + logInfo + "\n";
            FileUtils.writeStringToFile(new File(path), logInfo, Charset.defaultCharset(), true);

        } catch (IOException e) {
            LOGGER.warn("append log failed. log dir is {}", path);
        }
    }

    /**
     * 从logpath中解析出applicationId
     *
     * @param logPath
     * @return
     */
    public static String getApplicationIdFromSpark(String logPath) {
        File file = new File(logPath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (StringUtils.contains(line.toLowerCase(), "tracking url")
                        && StringUtils.contains(line.toLowerCase(), "proxy/application_")) {
                    String[] split = StringUtils.split(line, "/");
                    return split[split.length - 1];
                }
            }
        } catch (Exception e) {
            LOGGER.error("get application id from log error:{}", logPath, e);
        } finally {
            if (Objects.nonNull(br)) {
                IOUtils.closeQuietly(br);
            }
        }

        return "";
    }

    public static String CmdToFile(String path, String cmd) throws IOException {

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }

        // write sql to file. fileName format: md5(hql)_yyyyMMdd.sql
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String date = df.format(new Date());
        String cmdPath = path + DigestUtils.md5Hex(cmd.getBytes()) + "_" + date + ".sh";

        FileWriter fw = new FileWriter(cmdPath);
        fw.write(cmd);
        fw.flush();
        fw.close();

        return cmdPath;
    }

    public static void appendLog(String logPath, String info, Object... vars) {
        Pattern pattern = Pattern.compile("\\{\\}");
        final Matcher matcher = pattern.matcher(info);
        int i = 0;
        while (matcher.find()) {
            info = info.replaceFirst("\\{\\}", vars[i++].toString());
        }

        appendLog(logPath, info);
    }


    public static Process exchangeUserAndExecute(String logPath, String user, String command) {
        Process process = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec("sudo su - " + user);
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("whoami\n");
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
        } catch (Exception e) {
            appendLog(logPath, "task failed caused by {}", e);
            throw new RuntimeException(e);
        }
        return process;
    }


}
