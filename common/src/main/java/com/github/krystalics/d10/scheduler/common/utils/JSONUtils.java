package com.github.krystalics.d10.scheduler.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @description: json工具
 * @author: cyp
 * @create: 2018-12-17
 **/
public class JSONUtils {

    /**
     * 去除循环检测
     * @param obj
     * @return
     */
    public static String toJSONStringWithoutCircleDetect(Object obj){
        return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
    }
}
