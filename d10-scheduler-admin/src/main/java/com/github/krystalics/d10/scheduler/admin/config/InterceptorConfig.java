package com.github.krystalics.d10.scheduler.admin.config;

import com.github.krystalics.d10.scheduler.admin.interceptor.BaseLoginInterceptor;
import com.github.krystalics.d10.scheduler.admin.interceptor.TokenInterceptor;
import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 配置
 * @author krysta
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


	@Bean
	public BaseLoginInterceptor baseLoginInterceptor() {
		return new BaseLoginInterceptor();
	}


	@Autowired
	private TokenInterceptor tokenInterceptor;

	/**
	 * 注意路径后必须加 /** 否则拦截不生效
	 * @param registry
	 */
	@Override
    public void addInterceptors(InterceptorRegistry registry) {

    	List<String> excludePatchPatterns = Lists.newArrayList();
		/**
		 * 所有请求中除了以下几种、都直接转发
		 */
		excludePatchPatterns.add("/callback");
		excludePatchPatterns.add("/logoutCallback");
		excludePatchPatterns.add("/alive");
		excludePatchPatterns.add("/open/**");
		excludePatchPatterns.add("/api/**");
		excludePatchPatterns.add("/static/**");

//		registry.addInterceptor(leaderInterceptor())
//                .addPathPatterns("/**")
//		.excludePathPatterns(excludePatchPatterns);

		//对于所有api的请求都需要token验证
//		registry.addInterceptor(tokenInterceptor).addPathPatterns("/api/**");
	}

}
