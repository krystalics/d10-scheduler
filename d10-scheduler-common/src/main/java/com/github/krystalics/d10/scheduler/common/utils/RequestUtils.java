package com.github.krystalics.d10.scheduler.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: cyp
 * @create: 2021-01-22
 **/
public class RequestUtils {

	/**
	 * 获取所有请求参数
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(HttpServletRequest request){
		Map<String, String> map = new HashMap<>();
		Enumeration<String> enums = request.getParameterNames();
		while (enums.hasMoreElements()){
			String name = enums.nextElement();
			String value = request.getParameter(name);

			//形成键值对应的map
			map.put(name, value);
		}
		return map;

	}

}
