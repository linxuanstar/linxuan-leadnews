package com.linxuan.search.interceptor;

import com.linxuan.model.user.pojos.ApUser;
import com.linxuan.utils.thread.AppThreadLocalUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AppTokenInterceptor implements HandlerInterceptor {

    /**
     * 获取请求头中的userId信息并存入当前线程
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("userId");
        if (userId != null) {
            ApUser apUser = new ApUser();
            apUser.setId(Integer.parseInt(userId));
            AppThreadLocalUtil.setUser(apUser);
        }
        return true;
    }

    /**
     * 清理线程中数据,如果是postHandle可能会导致抛出异常导致无法清理
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // ApThreadLocalUtil.clear();
    }

    /**
     * 清理线程中数据
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AppThreadLocalUtil.clear();
    }
}

