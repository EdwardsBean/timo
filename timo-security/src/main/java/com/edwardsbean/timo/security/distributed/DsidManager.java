package com.edwardsbean.timo.security.distributed;

import com.edwardsbean.timo.common.AppConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 管理分布式session id,提供获取和生成方法
 * Created by shicongyu01_91 on 2015/4/24.
 */
public class DsidManager {
    public static Logger logger           = LoggerFactory.getLogger(DsidManager.class);
    /** cookie的key */
    private static String       dsidCookieName   = "superdsid";
    /** cookie的domain */
    private static String       dsidCookieDomain = "";
    /** cookie的path */
    private static String       dsidCookiePath   = "/";
    /** cookie过期时间,单位为秒,默认为-1永不过期 */
    private static int          dsidCookieMaxAge = -1;

    /** request */
    private HttpServletRequest request;
    /** response */
    private HttpServletResponse response;

    public DsidManager(HttpServletRequest request, HttpServletResponse response) {
        super();
        this.request = request;
        this.response = response;
    }

    /**
     * 获取分布式sessionId
     * @param newDsid：true表示创建新的；false表示分布式sessionId存在则返回，不存在则创建信分布式session id
     */
    public String getDsid(boolean newDsid) {
        if (newDsid) {
            return generateDsid();
        } else {
            return getDsid();
        }
    }

    /**
     * 获取分布式sessionId,若不存在则创建
     */
    public String getDsid() {
        Cookie cookies[] = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i] != null && StringUtils.equals(cookies[i].getName(), dsidCookieName)) {
                return cookies[i].getValue();
            }
        }
        return generateDsid();
    }

    /**
     * 创建分布式缓存的id
     */
    private String generateDsid() {
        String dsid = UUID.randomUUID().toString();//创建sessionid
        generateDsidCookie(dsid);
        return dsid;
    }

    /**
     * 创建分布式缓存的cookie
     */
    private void generateDsidCookie(String dsid) {
        Cookie mycookies = new Cookie(dsidCookieName, dsid);//创建cookie
        if (dsidCookieMaxAge > 0) {
            mycookies.setMaxAge(dsidCookieMaxAge);//设置过期时间
        }
        if (dsidCookieDomain != null && dsidCookieDomain.length() > 0) {//设置domain
            mycookies.setDomain(dsidCookieDomain);
        }
        mycookies.setPath(dsidCookiePath);//设置path
        response.addCookie(mycookies);
    }

    /**
     * 从配置文件读取cookie配置属性
     */
    static {
        String dsidName = AppConfigUtil.getProperty("cookie.dsid.name"); //cookie的key
        String dsidDomain = AppConfigUtil.getProperty("cookie.dsid.domain"); //cookie的domain
        String dsidPath = AppConfigUtil.getProperty("cookie.dsid.path");//cookie的path
        String dsidMaxAge = AppConfigUtil.getProperty("cookie.dsid.maxAge");//cookie的失效时间
        if (StringUtils.isNotBlank(dsidName)) {
            dsidCookieName = dsidName;
        }
        if (StringUtils.isNotBlank(dsidDomain)) {
            dsidCookieDomain = dsidDomain;
        }
        if (StringUtils.isNotBlank(dsidPath)) {
            dsidCookiePath = dsidPath;
        }
        if (StringUtils.isNotBlank(dsidMaxAge)) {
            try {
                dsidCookieMaxAge = Integer.parseInt(dsidMaxAge);
            } catch (Exception e) {
                logger.warn("分布式缓存cookie的过期时间设置有误，为：{},异常{}", dsidMaxAge, e);
            }
        }
    }
}
