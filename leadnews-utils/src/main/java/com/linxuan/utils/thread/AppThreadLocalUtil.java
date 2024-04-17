package com.linxuan.utils.thread;

import com.linxuan.model.user.pojos.ApUser;

public class AppThreadLocalUtil {
    private static final ThreadLocal<ApUser> AP_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 添加用户
     *
     * @param user
     */
    public static void setUser(ApUser user) {
        AP_USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取用户
     *
     * @return
     */
    public static ApUser getUser() {
        return AP_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理
     */
    public static void clear() {
        AP_USER_THREAD_LOCAL.remove();
    }
}
