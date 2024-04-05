package com.linxuan.utils.thread;

import com.linxuan.model.wemedia.pojos.WmUser;

public class WmThreadLocalUtil {

    private static final ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 添加用户
     *
     * @param user
     */
    public static void setUser(WmUser user) {
        WM_USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取用户
     *
     * @return
     */
    public static WmUser getUser() {
        return WM_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理
     */
    public static void clear() {
        WM_USER_THREAD_LOCAL.remove();
    }
}
