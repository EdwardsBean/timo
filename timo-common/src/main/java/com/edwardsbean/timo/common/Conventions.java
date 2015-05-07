package com.edwardsbean.timo.common;

/**
 * 常量配置类，主要是一些惯例的KEY
 *
 */
public class Conventions {
    /**
     * REST服务URL的固定开头
     */
    public static final String SERVICES_URL_PREFIX   = "/services";
    /**
     * 远程调用时用来传递统一上下文UUID的HTTP HEADER
     */
    public static final String LOG_ID_HEADER         = "LOGID";
    /**
     * 在MDC中存放统一上下文LOGID的KEY
     */
    public static final String CTX_LOG_ID_MDC        = "ctxLogId";
    /**
     * 远程调用时用于传递调用方系统名的HTTP HEADER
     */
    public static final String SRC_SYS_HEADER        = "X-SRC-SYS";
    /**
     * 在MDC中存放远程调用来源系统的KEY
     */
    public static final String CTX_SRC_SYS_MDC       = "ctxSrcSys";
    /**
     * 配置文件中存放系统名的属性KEY
     */
    public static final String APP_NAME_PROPERTY_KEY = "app.name";
    /**
     * 配置文件中存放系统运行模式的属性KEY
     */
    public static final String RUN_MODE_PROPERTY_KEY = "run.mode";
    /**
     * 分布式事务的TXID
     */
    public static final String TX_ID_KEY             = "X-TX-ID";
    /**
     * HTTP头中存放请求来源IP
     */
    public static final String REAL_IP_HEADER        = "X-Real-IP";
    /**
     * HTTP头中存放客户真实IP
     */
    public static final String CLIENT_IP_HEADER      = "CLIENTIP";
    /**
     * 运行模式：生产模式
     */
    public static final String MODE_PROD             = "prod";
    /**
     * 版本号前缀
     */
    public static final String VERSION_PREFIX       = "v";
    /*
     * HTTP头中存放客户端版本号
     */
    public static final String VERSION              = "VERSION";
}
