package com.edwardsbean.timo.security.distributed;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * 分布式session对应的request，封装Session的获取过程，取分布式session
 */
public class DisHttpServletRequest extends HttpServletRequestWrapper {
    /** 分布式session wrapper*/
    private DisHttpSession session;
    /** 分布式 */
    private DsidManager    dsidManager;

    public DisHttpServletRequest(DsidManager dsidManager, HttpServletRequest request) {
        super(request);
        this.dsidManager = dsidManager;
        //初始化session wrapper
        session = new DisHttpSession(dsidManager.getDsid(), super.getSession());
    }

    /**
     * 创建并获取分布式session
     * （create为true时，这里不删除分布式缓存中的旧session数据，因为可能其他服务器正在使用，让缓存中的session数据自动过期）
     * @see javax.servlet.http.HttpServletRequestWrapper#getSession(boolean)
     */
    public HttpSession getSession(boolean create) {
        if (create) {
            synchronized (this) {
                if (create) {
                    this.session.syncCacheSessionData();
                    this.session = new DisHttpSession(dsidManager.getDsid(true),
                            super.getSession(true));
                }
            }
        }
        return this.session;
    }

    public HttpSession getSession() {
        return getSession(false);
    }
}
