package com.github.krystalics.d10.scheduler.common.service;

import com.github.krystalics.d10.scheduler.common.session.SessionUser;

/**
 * @author linjiabao001
 * @date 2021/10/7
 * @description
 */
public interface UserService {

    /**
     * 用户登陆
     * @param userId
     * @return
     */
    SessionUser login(long userId);

    /**
     * 从缓存中获取数据
     * @param userId
     * @return
     */
    SessionUser getSessionUser(long userId);

    /**
     * 情况session
     * @param userId
     */
    void clearSession(long userId);

}
