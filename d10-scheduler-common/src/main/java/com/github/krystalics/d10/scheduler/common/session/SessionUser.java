package com.github.krystalics.d10.scheduler.common.session;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: cyp
 * @create: 2019-07-26
 **/
@Data
@ToString
public class SessionUser implements Serializable {

    private Long id;

    private Long userCode;

    private String name;

    private String token;

    private Set<String> authSet;

    private Map<String, Object> extendMap = new HashMap<>();

    public void addExtendParam(String key, Object value){
		extendMap.put(key, value);
	}


}
