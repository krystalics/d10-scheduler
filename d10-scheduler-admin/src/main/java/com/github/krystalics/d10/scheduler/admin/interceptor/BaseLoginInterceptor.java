package com.github.krystalics.d10.scheduler.admin.interceptor;

import com.github.krystalics.d10.scheduler.common.anotation.IgnoreLogin;
import com.github.krystalics.d10.scheduler.common.service.UserService;
import com.github.krystalics.d10.scheduler.common.session.SessionContext;
import com.github.krystalics.d10.scheduler.common.session.SessionUser;
import com.github.krystalics.d10.scheduler.common.utils.CookieUtils;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 管理员权限验证
 *
 */
@NoArgsConstructor
public class BaseLoginInterceptor implements HandlerInterceptor {


	private static final Logger LOG = LoggerFactory.getLogger(BaseLoginInterceptor.class);

	@Autowired
	private UserService userService;

	@Override
	public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {

		SessionContext.removeSessionUser();
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

		String requestURI = request.getRequestURI();
		/**
		 * 校验是否忽略登陆
		 */
		if (handler instanceof HandlerMethod) {
			Method method = ((HandlerMethod) handler).getMethod();
			IgnoreLogin ignoreLogin = method.getAnnotation(IgnoreLogin.class);
			if(Objects.nonNull(ignoreLogin)){
				LOG.info("ignore login path:{}", requestURI);
				return true;
			}
		}

		String userCookieInfo = CookieUtils.getUserCookieInfo(request);

		/**
         * 无登陆用户
         */
		Long userId = CookieUtils.getUserId(userCookieInfo);
		if(userId == null){
//			todo  PassportUtils是和统一登陆结合的部分、暂时不需要
//			ResponseUtils.notLogin(request, response, PassportUtils.getServiceLoginUrl());
			return false;
		}

		SessionUser user = userService.getSessionUser(userId);
		if(user == null){

//			ResponseUtils.notLogin(request, response, PassportUtils.getServiceLoginUrl());
			return false;
		}

		user.setToken(userCookieInfo);

        SessionContext.setSessionUser(user);

		return true;

	}


}
