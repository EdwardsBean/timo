package com.edwardsbean.timo.security.distributed;

import java.util.Map;

import net.rubyeye.xmemcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * memcached保存session数据的service
 * 需要在spring中通过构造注入(注入memcachedClient)，使用时请使用静态属性instance
 * @author chenxingbo
 * @version $Id: MemcachedSessionService.java, v 0.1 2014年7月25日 下午3:38:54 chenxingbo Exp $
 */
public class MemcachedSessionService {
    public static Logger                  logger                = LoggerFactory
            .getLogger(MemcachedSessionService.class);
    /** 单例的示例对象 */
    public static MemcachedSessionService instance              = null;
    /** memcached客户端 */
    private MemcachedClient               memcachedClient;
    /** memcached中的session过期时间, 默认30分钟 */
    private int                           sessionExpiredSeconds = 30 * 60;
    private String                        keyPrefix             = "SUPER_DSID_";

    public MemcachedSessionService(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
        instance = this;
    }

    /**
     * 从memcached中获取缓存，如果session存在，则返回
     */
    public Map<String, Object> getSession(String id) {
        try {
            return memcachedClient.getAndTouch(getKey(id), sessionExpiredSeconds);
        } catch (Exception e) {
            logger.warn("查询分布式session:[{}]缓存数据时，发生异常{}！", id, e);
        }
        return null;
    }

    /**
     * 保存session到memcached中
     */
    public void saveSession(String id, Map<String, Object> session) {
        try {
            memcachedClient.set(getKey(id), sessionExpiredSeconds, session);
        } catch (Exception e) {
            logger.warn("保存分布式session:[{}]的缓存数据[{}]时,发生异常{}！", id, session, e);
        }
    }

    /**
     * 删除memcached中的session
     * @param id
     */
    public void removeSession(String id) {
        try {
            memcachedClient.delete(getKey(id));
        } catch (Exception e) {
            logger.warn("删除分布式session:[{}]时发生异常{}！", id, e);
        }
    }

    private String getKey(String dsid) {
        return keyPrefix + dsid;
    }

    public void setSessionExpiredSeconds(int sessionExpiredSeconds) {
        this.sessionExpiredSeconds = sessionExpiredSeconds;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}
