package com.edwardsbean.timo.security.distributed;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

/**
 * wrap HttpSession
 * Created by shicongyu01_91 on 2015/4/24.
 */
public class HttpSessionWrapper implements HttpSession {

    private HttpSession originSession;

    /**
     * @param originSession
     */
    public HttpSessionWrapper(HttpSession originSession) {
        super();
        this.originSession = originSession;
    }

    /**
     * @see javax.servlet.http.HttpSession#getCreationTime()
     */
    @Override
    public long getCreationTime() {
        return originSession.getCreationTime();
    }

    /**
     * @see javax.servlet.http.HttpSession#getId()
     */
    @Override
    public String getId() {
        return originSession.getId();
    }

    /**
     * @see javax.servlet.http.HttpSession#getLastAccessedTime()
     */
    @Override
    public long getLastAccessedTime() {
        return originSession.getLastAccessedTime();
    }

    /**
     * @see javax.servlet.http.HttpSession#getServletContext()
     */
    @Override
    public ServletContext getServletContext() {
        return originSession.getServletContext();
    }

    /**
     * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
     */
    @Override
    public void setMaxInactiveInterval(int interval) {
        originSession.setMaxInactiveInterval(interval);
    }

    /**
     * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
     */
    @Override
    public int getMaxInactiveInterval() {
        return originSession.getMaxInactiveInterval();
    }

    /**
     * @see javax.servlet.http.HttpSession#getSessionContext()
     */
    @Override
    public HttpSessionContext getSessionContext() {
        return originSession.getSessionContext();
    }

    /**
     * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String name) {
        return originSession.getAttribute(name);
    }

    /**
     * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
     */
    @Override
    public Object getValue(String name) {
        return originSession.getValue(name);
    }

    /**
     * @see javax.servlet.http.HttpSession#getAttributeNames()
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        return originSession.getAttributeNames();
    }

    /**
     * @see javax.servlet.http.HttpSession#getValueNames()
     */
    @Override
    public String[] getValueNames() {
        return originSession.getValueNames();
    }

    /**
     * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(String name, Object value) {
        originSession.setAttribute(name, value);
    }

    /**
     * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
     */
    @Override
    public void putValue(String name, Object value) {
        originSession.putValue(name, value);
    }

    /**
     * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(String name) {
        originSession.removeAttribute(name);
    }

    /**
     * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
     */
    @Override
    public void removeValue(String name) {
        originSession.removeValue(name);
    }

    /**
     * @see javax.servlet.http.HttpSession#invalidate()
     */
    @Override
    public void invalidate() {
        originSession.invalidate();
    }

    /**
     * @see javax.servlet.http.HttpSession#isNew()
     */
    @Override
    public boolean isNew() {
        return originSession.isNew();
    }

    public HttpSession getOriginSession() {
        return originSession;
    }

    public void setOriginSession(HttpSession originSession) {
        this.originSession = originSession;
    }
}

