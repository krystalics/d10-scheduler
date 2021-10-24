package com.github.krystalics.d10.scheduler.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * cookie
 * @author krysta
 */
@Slf4j
public class CookieUtils {


	private static final String AES_BASE_TOKEN = "3SD2RcbRRcLXpfszuq5jACBYMqHRQ3W6";

    private static final String COOKIE_SPLIT = "&";

	public static final int COOKIE_EXPIRE_TIME = 60 * 60;

	public static final String USER_COOKIE_NAME = "webBaseUserInfo";

	/**
	 * 从请求中获取用户信息
	 * @param request   请求
	 * @return
	 */
	public static String getUserCookieInfo(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length < 1) {
			return null;
		}
		String cookieInfo = null;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(USER_COOKIE_NAME)) {
				cookieInfo = cookie.getValue();
				return cookieInfo;
			}
		}
		return null;
	}

	/**
     * 将userId写入cookie
     * @param userId        用户id
     * @param response      响应
     * @param expiry        过期时间
     */
    public static void writeUserCookie(long userId, HttpServletResponse response, int expiry) throws Exception {
        Cookie cookie = new Cookie(USER_COOKIE_NAME, generateLoginToken(userId));
        cookie.setMaxAge(expiry);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static String generateLoginToken(long userId){
		return AESUtils.encrypt(userId + COOKIE_SPLIT + System.currentTimeMillis(), AES_BASE_TOKEN);
	}

    public static Long getUserId(String cookieInfo){
		if(StringUtils.isEmpty(cookieInfo)){
			return null;
		}
		String cookieStr = AESUtils.decrypt(cookieInfo, AES_BASE_TOKEN);
		if(StringUtils.isEmpty(cookieStr)){
			return null;
		}
		String[] split = StringUtils.split(cookieStr, COOKIE_SPLIT);
		return Long.parseLong(split[0]);
	}


    /**
     * 删除cookie
     *
     * @param response      响应
     */
    public static void removeUserCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(USER_COOKIE_NAME, "");
        cookie.setMaxAge(100);
        cookie.setPath("/");
        response.addCookie(cookie);
    }


}
