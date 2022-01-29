package com.github.krystalics.d10.scheduler.common.service.impl;

import com.github.krystalics.d10.scheduler.common.service.UserService;
import com.github.krystalics.d10.scheduler.common.session.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author linjiabao001
 * @date 2021/10/7
 * @description
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

//    @Autowired
//    private RedissonClient client;

    @Override
    public SessionUser login(long userId) {
        return null;
    }

    @Override
    public SessionUser getSessionUser(long userId) {
        return null;
    }

    @Override
    public void clearSession(long userId) {

    }
}
