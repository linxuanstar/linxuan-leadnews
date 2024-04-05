package com.linxuan.wemedia.interceptor;

import com.linxuan.model.wemedia.pojos.WmUser;
import com.linxuan.utils.thread.WmThreadLocalUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class WmTokenInterceptor implements HandlerInterceptor {


    /**
     * 获取请求头中的userId信息并存入当前线程
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("userId");
        if (userId != null) {
            WmUser wmUser = new WmUser();
            wmUser.setId(Integer.parseInt(userId));
            WmThreadLocalUtil.setUser(wmUser);
        }
        return true;
    }

    /**
     * 清理线程中数据
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        WmThreadLocalUtil.clear();
    }
}
