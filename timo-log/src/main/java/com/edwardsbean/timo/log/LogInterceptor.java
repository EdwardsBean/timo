package com.edwardsbean.timo.log;

import com.edwardsbean.timo.common.Conventions;
import com.edwardsbean.timo.common.context.ServiceContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 记录每个控制器调用时间
 * Created by shicongyu01_91 on 2015/4/7.
 */
public class LogInterceptor extends HandlerInterceptorAdapter {
    protected final static Logger log = LoggerFactory.getLogger(LogInterceptor.class.getName());
    protected boolean                        printArguments = true;
    protected boolean                        printResults   = true;
    protected NamedThreadLocal<Long>  startTimeThreadLocal =
            new NamedThreadLocal<Long>("StopWatch-StartTime");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        WebDigestLogAdditionalInfoUtil.clear();
        long beginTime = System.currentTimeMillis();
        startTimeThreadLocal.set(beginTime);
        return true;
    }

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
        log.info("[({},{})({},{},{},{},{}ms)({})({},{})]",
                StringUtils.defaultString(ServiceContextHolder.get(Conventions.REAL_IP_HEADER), "-"),
                StringUtils.defaultString(ServiceContextHolder.get(Conventions.CLIENT_IP_HEADER), "-"),
                request.getMethod(), request.getServletPath(), handlerName, result,consumeTime,
                getArgumentsString(), getResponseStatus(response), getResultsString());
    }


    /**
     * 处理被CGLIB增强过的类名，例如：CustomerServicesController$$EnhancerByCGLIB$$339ba907
     */
    protected String getRealClassName(String className) {
        if (StringUtils.contains(className, "$$EnhancerByCGLIB")) {
            return StringUtils.split(className, "$$")[0];
        }
        return className;
    }

    protected String getResultFlag(HttpServletResponse response, Exception ex) {
        String result = ex == null ? "Y" : "N";
        if (StringUtils.equals("Y", result) && response != null
                && response.getStatus() >= HttpServletResponse.SC_BAD_REQUEST) {
            result = "N";
        }
        return result;
    }

    protected String getResponseStatus(HttpServletResponse response) {
        String status = "-";
        if (response != null) {
            status = Integer.toString(response.getStatus());
        }
        return status;
    }

    protected Object getArgumentsString() {
        if (printArguments) {
            return StringUtils.join(WebDigestLogAdditionalInfoUtil.getParameters(), ",");
        } else {
            return "-";
        }
    }

    protected Object getResultsString() {
        if (printResults) {
            return StringUtils.join(WebDigestLogAdditionalInfoUtil.getResults(), ",");
        } else {
            return "-";
        }
    }

    public void setPrintArguments(boolean printArguments) {
        this.printArguments = printArguments;
    }

    public void setPrintResults(boolean printResults) {
        this.printResults = printResults;
    }
}
