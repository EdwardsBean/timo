package com.edwardsbean.timo.security.distributed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 分布式session的filter
 * Created by shicongyu01_91 on 2015/4/24.
 */
public class DisSessionFilter extends HttpServlet implements Filter {
    public static Logger logger           = LoggerFactory.getLogger(DisSessionFilter.class);
    private static final long serialVersionUID = 2859796461869583139L;

    /**
     * 获取或创建分布式session cookie
     * 用支持分布式session的DisHttpServletRequest替代servletRequest
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        DsidManager dsidManager = new DsidManager(request, response);
        //wrap 支持分布式session的request
        DisHttpServletRequest disRequest = new DisHttpServletRequest(dsidManager, request);
        filterChain.doFilter(disRequest, servletResponse);
        //同步session数据到分布式缓存
        ((DisHttpSession) disRequest.getSession()).syncCacheSessionData();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}