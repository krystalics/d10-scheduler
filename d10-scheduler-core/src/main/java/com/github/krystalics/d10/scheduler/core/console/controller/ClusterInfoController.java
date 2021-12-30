package com.github.krystalics.d10.scheduler.core.console.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
@RestController
@RequestMapping("/cluster")
public class ClusterInfoController {
    @GetMapping("/info")
    public String clusterInfo() {
        return ClusterInfo.getClusterInfo().toString();
    }
}
