package com.github.krystalics.d10.scheduler.common.utils;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

public class ResponseUtils {

	/**
	 * 消息体
	 */
	public static final String DATA = "data";

	public static final String MSG = "msg";

	public static final String TOTAL = "total";

	public static final String CODE = "code";
	/**
	 * 状态码
	 */
	public static final int CODE_SUCCESS = 1;

	public static final int CODE_ERROR = -1;

	public static final int CODE_NOT_LOGIN = -999;

	public static final int STATUS_REDIRECT = 302;

	public static final String MSG_NO_LOGIN = "NOT_LOGIN";


	/**
	 * 返回记录总数和数据列表
	 *
	 * @param total 记录总数
	 * @param data  数据
	 * @return
	 */
	public static String list(int total, Object data) {
		JSONObject result = new JSONObject();
		result.put(CODE, CODE_SUCCESS);
		result.put(TOTAL, total);
		result.put(DATA, data);
		return JSONUtils.toJSONStringWithoutCircleDetect(result);
	}

	/**
	 * 空列表
	 *
	 * @return
	 */
	public static String listEmpty() {
		return list(0, Collections.emptyList());
	}

	/**
	 * 返回数据
	 *
	 * @param data 数据
	 * @return
	 */
	public static String success(Object data) {
		JSONObject result = new JSONObject();
		result.put(CODE, CODE_SUCCESS);
		result.put(DATA, data);
		return JSONUtils.toJSONStringWithoutCircleDetect(result);
	}

	/**
	 * 返回成功消息
	 *
	 * @param msg 提示信息
	 * @return
	 */
	public static String success(String msg) {
		JSONObject result = new JSONObject();
		result.put(CODE, CODE_SUCCESS);
		result.put(DATA, msg);
		return JSONUtils.toJSONStringWithoutCircleDetect(result);
	}

	/**
	 * 重定向
	 *
	 * @param url
	 * @return
	 */
	public static String ajaxRedirect(String url) {
		JSONObject result = new JSONObject();
		result.put(CODE, STATUS_REDIRECT);
		result.put(DATA, url);
		return JSONUtils.toJSONStringWithoutCircleDetect(result);
	}

	/**
	 * 返回错误消息
	 *
	 * @param msg 提示信息
	 * @return
	 */
	public static String error(String msg) {
		return error(CODE_ERROR, msg);
	}

	/**
	 * 携带错误码和错误信息
	 *
	 * @param code
	 * @param msg
	 * @return
	 */
	public static String error(int code, String msg) {
		JSONObject result = new JSONObject();
		result.put(CODE, code);
		result.put(MSG, msg);
		return JSONUtils.toJSONStringWithoutCircleDetect(result);
	}

	/**
	 * 没有权限写入信息
	 *
	 * @param request
	 * @param response
	 */
//    public static void noAuth(HttpServletRequest request, HttpServletResponse response) {
//        errorMsg(request, response, STATUS_NO_AUTH, MSG_NO_AUTH, CommonConstants.PAGE_NO_AUTH);
//    }
//    public static void noAuth(HttpServletRequest request, HttpServletResponse response, String url) {
//        errorMsg(request, response, STATUS_NO_AUTH, MSG_NO_AUTH, url);
//    }
//    public static String getNoAuthUrl(String ucId){
//        return CommonConstants.PAGE_NO_AUTH + CommonConstants.QUESTION + "ucId"  + CommonConstants.EQUAL + ucId;
//    }
//

	/**
	 * 未登录写入信息
	 *
	 * @param request
	 * @param response
	 */
	public static void notLogin(HttpServletRequest request, HttpServletResponse response, String redirectUrl) {
		errorMsg(request, response, CODE_NOT_LOGIN, MSG_NO_LOGIN, redirectUrl);
	}

	public static String notLogin() {
		JSONObject result = new JSONObject();
		result.put(CODE, CODE_NOT_LOGIN);
		result.put(MSG, MSG_NO_LOGIN);
		return JSONUtils.toJSONStringWithoutCircleDetect(result);
	}

	public static void errorMsg(HttpServletRequest request, HttpServletResponse response, int code, String msg, String redirectUrl) {
		if (isRequestAjax(request)) {
			response.reset();
			response.setContentType("application/X-EPX;charset=UTF-8");
			PrintWriter printWriter = null;
			try {
				printWriter = response.getWriter();
				printWriter.write(error(code, msg));
				printWriter.flush();
			} catch (IOException e) {

			} finally {
				if (printWriter != null) {
					printWriter.close();
				}
			}
		} else {
			try {
				response.sendRedirect(redirectUrl);
			} catch (IOException e) {

			}
		}
	}

	public static boolean isRequestAjax(HttpServletRequest request) {

		String accept = request.getHeader("accept");
		if (accept != null && accept.indexOf("application/json") != -1) {
			return true;
		}

		String xRequestedWith = request.getHeader("X-Requested-With");
		if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1) {
			return true;
		}
		return false;
	}

}
