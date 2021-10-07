package com.github.krystalics.d10.scheduler.common.session;


/**
 * @description: session上下文
 * @author: cyp
 * @create: 2019-07-26
 **/
public class SessionContext {

    private static final ThreadLocal<SessionUser> SESSION_USER_HOLDER = new ThreadLocal<>();

    public static void setSessionUser(SessionUser sessionUser){
        SESSION_USER_HOLDER.set(sessionUser);
    }

    public static void removeSessionUser(){
        SESSION_USER_HOLDER.remove();
    }

    public static SessionUser getSessionUser(){
        return SESSION_USER_HOLDER.get();
    }
}
