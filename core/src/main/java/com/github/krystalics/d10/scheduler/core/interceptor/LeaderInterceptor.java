package com.github.krystalics.d10.scheduler.core.interceptor;

import com.github.krystalics.d10.scheduler.core.common.ClusterInfo;
import com.github.krystalics.d10.scheduler.core.utils.LeaderUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description 判断自身是否为leader、若不是则转发给leader
 */
@NoArgsConstructor
@Slf4j
public class LeaderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        //todo 节点与zk失联策略
        if (ClusterInfo.getLostState()) {
            log.error("i can't connect to zk, fuck");

        }

        //todo 节点是leader
        if(LeaderUtils.isLeader()){
            log.info("i'm the leader,to process something special");
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
