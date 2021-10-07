package com.github.krystalics.d10.scheduler.core.interceptor;

import com.github.krystalics.d10.scheduler.core.common.ClusterInfo;
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

        }


        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
