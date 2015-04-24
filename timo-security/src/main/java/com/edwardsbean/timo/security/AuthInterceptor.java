package com.edwardsbean.timo.security;

import com.edwardsbean.timo.security.session.SessionDataGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    private SessionDataGetter sessionDataGetter;

    public static final String                                 HMSS_KEY                   = "HMSS";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            AuthPassport authPassport = ((HandlerMethod) handler).getMethodAnnotation(AuthPassport.class);

            //没有声明需要权限,或者声明不验证权限
            if (authPassport == null || !authPassport.validate())
                return true;

            Boolean allow = null;

            //
            if (request.getSession().getAttribute("username") != null) {
                allow = true;
            } else {
                allow = false;
            }

            if (!allow) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return allow;
        } else
            return true;
    }
}
