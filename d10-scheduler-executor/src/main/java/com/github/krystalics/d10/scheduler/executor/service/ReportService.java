package com.github.krystalics.d10.scheduler.executor.service;

import com.github.krystalics.d10.scheduler.dao.mapper.NodeMapper;
import com.github.krystalics.d10.scheduler.executor.utils.ReportUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author linjiabao001
 * @date 2022/2/3
 * @description
 */
@Service
@Slf4j
public class ReportService implements CommandLineRunner {

    @Autowired
    private NodeMapper nodeMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("first report");
        nodeMapper.insert(ReportUtils.nodeInfo());
    }


    @Scheduled(cron = "*/30 * * * * ")
    public void report(){
        log.info("common report");
        nodeMapper.insert(ReportUtils.nodeInfo());
    }
}
