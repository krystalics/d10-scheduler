package com.github.krystalics.d10.scheduler.core.interceptor;

import cn.hutool.crypto.digest.DigestUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 接口的拦截器
 * 可以自定义白名单，跳过拦截
 *
 * @author wangning040@ke.com
 * @date 2020-12-12 12:38
 */
@Component
@ConfigurationProperties(prefix = "token")
public class TokenInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);
	// 请求超时时间
	private static final long EXPIRE_TIME = 5 * 60 * 1000L;
	private Map<String, String> keyMap;

	public void setKeyMap(Map<String, String> keyMap) {
		this.keyMap = keyMap;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		logger.info("Key map={}", keyMap);
		logger.info("Request URI: {}", request.getRequestURI());
		try {
			String appId = request.getHeader("appId");
			String timestamp = request.getHeader("timestamp");
			String sign = request.getHeader("sign");
			Assert.isTrue(StringUtils.isNotBlank(appId), "AppId is required");
			Assert.isTrue(StringUtils.isNotBlank(timestamp), "Timestamp is required");
			Assert.isTrue(StringUtils.isNotBlank(sign), "Sign is required");
			// 校验请求是否超时
			long requestInterval = System.currentTimeMillis() - Long.parseLong(timestamp);
			Assert.isTrue(requestInterval < EXPIRE_TIME, "Request timeout. Please try again later");
			// 校验 appId 是否存在
			Assert.notNull(keyMap.get(appId), "AppId is not exist");
			// 校验签名是否正确
			String plaintext = request.getRequestURL() + keyMap.get(appId) + timestamp;
			String cipher = DigestUtil.md5Hex(plaintext.getBytes(StandardCharsets.UTF_8));
			Assert.isTrue(cipher.equals(sign), "Signature error");
			return true;
		} catch (Exception e) {
			response.addHeader("error", e.getMessage());
			throw e;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

	}

}
