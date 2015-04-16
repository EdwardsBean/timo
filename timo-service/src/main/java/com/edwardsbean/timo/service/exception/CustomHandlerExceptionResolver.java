package com.edwardsbean.timo.service.exception;

import com.edwardsbean.timo.service.model.Msg;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 控制器全局异常处理
 * Created by edwardsbean on 14-10-29.
 */
public class CustomHandlerExceptionResolver extends DefaultHandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!(request.getHeader("accept").indexOf("application/json") > -1 || (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1) || request
                .getParameter("callback") != null)) {
            //同步请求
            //TODO:添加异常处理
            super.resolveException(request, response, handler, ex);
        } else {
            // 异步请求
            try {
                PrintWriter writer = response.getWriter();
                String callback = request.getParameter("callback");
                ObjectMapper mapper = new ObjectMapper();
                Msg msgCode = new Msg();
                msgCode.setCode("1");
                msgCode.setMsg(ex.getLocalizedMessage());
                String json = mapper.writeValueAsString(msgCode);
                if (callback != null) {
                    callback = callback + "(" + json + ")";
                    writer.write(callback);
                } else {
                    writer.write(json);
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
