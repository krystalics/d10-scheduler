package com.github.krystalics.d10.scheduler.executor.utils;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class StreamGobbler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamGobbler.class);
    private final InputStream inputStream;
    private final OutputStream os;
    private final String type;
    private Throwable throwable = null;

    public StreamGobbler(InputStream is, String type, OutputStream redirect) {
        this.inputStream = is;
        this.type = type;
        this.os = redirect;
    }

    @Override
    public void run() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(os, Charsets.UTF_8));
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                // 在读取输入流时发生异常，说明输入流有问题，不用在while循环中处理
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                // 为了解决在重定向过程中发生异常后导致不再消费输入流的问题，采用下面方案
                // 如果在重定向输出文件时发生异常，捕捉异常并关闭文件输出流，后续将输入流中数据重定向到标准输出中
                try {
                    if (writer != null) {
                        writer.write(line);
                        writer.newLine();
                        writer.flush();
                    } else {
                        // 后续将输出流重定向到标准输出中
                        System.out.println(type + ">" + line);
                    }
                } catch (Throwable e) {
                    LOGGER.warn("error happened when write log file", e);
                    throwable = e;
                    IOUtils.closeQuietly(writer);
                    writer = null;
                }
            }
        } catch (Throwable e) {
            LOGGER.warn("error happened when read stream", e);
            throwable = e;
        } finally {
            if (writer != null) {
                IOUtils.closeQuietly(writer);
            }
            // 如果整个过程发生异常，最后抛出
            if (throwable != null) {
                throw new RuntimeException(throwable);
            }
        }
    }
}
