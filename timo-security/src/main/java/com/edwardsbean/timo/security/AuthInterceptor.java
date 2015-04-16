package com.edwardsbean.timo.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证权限
 * Created by edwardsbean on 2015/4/8 0008.
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            AuthPassport authPassport = ((HandlerMethod) handler).getMethodAnnotation(AuthPassport.class);

            //没有声明需要权限,或者声明不验证权限
            if (authPassport == null || !authPassport.validate())
                return true;

            Boolean allow = null;
            //TODO: 由用户权限系统判断
            if (!allow) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return allow;
        } else
            return true;
    }
}
