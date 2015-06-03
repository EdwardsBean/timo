package com.edwardsbean.timo.service.exception;

import com.edwardsbean.timo.service.model.Msg;
import com.edwardsbean.timo.service.model.MsgCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 控制器全局异常处理
 * @author edwardsbean
 * @date 14-10-29.
 */
public class CustomHandlerExceptionResolver extends DefaultHandlerExceptionResolver {
    private static Logger logger = LoggerFactory.getLogger(CustomHandlerExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!(request.getHeader("accept") != null && request.getHeader("accept").indexOf("application/json") > -1 || (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1) || request
                .getParameter("callback") != null)) {
            //非Ajax请求
            //TODO:添加异常处理
            super.resolveException(request, response, handler, ex);
            writeJsonResult(response, ex, false, request);

        } else {
            // 异步请求
            writeJsonResult(response, ex, true, request);
        }
        return null;
    }

    private void writeJsonResult(HttpServletResponse response, Exception ex, boolean isAjax, HttpServletRequest request) {
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            ObjectMapper mapper = new ObjectMapper();
            Msg msgCode = new Msg(MsgCode.SYSTEM_ERROR.getCode(), ex.getLocalizedMessage());
            String callback = request.getParameter("callback");
            String json = mapper.writeValueAsString(msgCode);
            if (callback != null) {
                callback = callback + "(" + json + ")";
                writer.write(callback);
            } else {
                writer.write(json);
            }
            writer.flush();
        } catch (IOException e) {
            logger.error("Global exception handler error:",e);
        } finally {
            logger.error("Global exception log:",ex);
            if (writer != null) {
                writer.close();
            }
        }
    }
}
