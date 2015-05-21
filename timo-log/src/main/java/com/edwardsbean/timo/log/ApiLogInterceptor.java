package com.edwardsbean.timo.log;

import com.edwardsbean.timo.common.Conventions;
import com.edwardsbean.timo.common.context.ServiceContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author edwardsbean
 * @date 15-5-12
 */
public class ApiLogInterceptor extends LogInterceptor{
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long endTime = System.currentTimeMillis();
        long beginTime = startTimeThreadLocal.get();
        long consumeTime = endTime - beginTime;
        String handlerName = getRealClassName(handler.getClass().getName());
        String result = getResultFlag(response, ex);
        if (handler.getClass() == HandlerMethod.class) {
            HandlerMethod method = (HandlerMethod) handler;
            handlerName = getRealClassName(method.getBean().getClass().getSimpleName()) + "."
                    + method.getMethod().getName();
        }
        String divideVersion = request.getHeader(Conventions.DIVIDE_VERSION);
        String imei = request.getHeader(Conventions.IMEI);
        String imsi = request.getHeader(Conventions.IMSI);
        String pid = request.getHeader(Conventions.PID);


        log.info("[({},{})({},{})({},{},{},{},{}ms)({})({},{})]",
                divideVersion,imei,
                StringUtils.defaultString(ServiceContextHolder.get(Conventions.REAL_IP_HEADER), "-"),
                StringUtils.defaultString(ServiceContextHolder.get(Conventions.CLIENT_IP_HEADER), "-"),
                request.getMethod(), request.getServletPath(), handlerName, result, consumeTime,
                getArgumentsString(), getResponseStatus(response), getResultsString());
    }
}
