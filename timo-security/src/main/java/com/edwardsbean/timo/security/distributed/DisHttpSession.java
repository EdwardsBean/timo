package com.edwardsbean.timo.security.distributed;

import com.edwardsbean.timo.common.AppConfigUtil;
import com.google.common.collect.Sets;
import org.apache.commons.collections.iterators.EnumerationIterator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.savedrequest.Enumerator;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Set;

/**
 * 分布式session
 */
public class DisHttpSession extends HttpSessionWrapper {
    /** 分布式session id */
    private String                             dsid;
    /** 存储缓存数据 */
    private ChangeSensitiveMap<String, Object> cachedSessionDataMap = null;
    /** 從配置文件中获取需要分布式缓存的session key(配置文件中以正则表达式存在，以分号间隔) */
    private static String[]                    cachedSessionKeys    = getCachedSessionKeys();

    public DisHttpSession(String sid, HttpSession session) {
        super(session);
        this.dsid = sid;
        this.cachedSessionDataMap = new ChangeSensitiveMap<String, Object>(
                MemcachedSessionService.instance.getSession(sid));
    }

    public Object getAttribute(String name) {
        Object result = this.cachedSessionDataMap.get(name);
        if (result == null) {
            result = super.getAttribute(name);
        }
        return result;
    }

    public Enumeration<String> getAttributeNames() {
        return (new Enumerator<String>(getNames(), true));
    }

    public void invalidate() {
        this.cachedSessionDataMap.clear();
        super.invalidate();
    }

    public void removeAttribute(String name) {
        if (PatternMatchUtils.simpleMatch(cachedSessionKeys, name)) {
            this.cachedSessionDataMap.remove(name);
        }
        super.removeAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        if (PatternMatchUtils.simpleMatch(cachedSessionKeys, name)) {
            this.cachedSessionDataMap.put(name, value);
        }
        super.setAttribute(name, value);
    }

    /**
     */
    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    /**
     */
    @Override
    public String[] getValueNames() {
        return (String[]) getNames().toArray();
    }

    @SuppressWarnings("unchecked")
    private Set<String> getNames() {
        Set<String> names = this.cachedSessionDataMap.keySet();
        names.addAll(Sets.newHashSet(new EnumerationIterator(super.getAttributeNames())));
        return names;
    }

    /**
     * 從配置文件中获取需要分布式缓存的session key
     */
    private static String[] getCachedSessionKeys() {
        String cks = AppConfigUtil.getProperty("session.data.distributed.keys");
        if (StringUtils.isBlank(cks)) {
            return new String[] {};
        }
        return cks.split(";");
    }

    /**
     * 同步缓存数据到分布式缓存中
     */
    public void syncCacheSessionData() {
        //数据有变更，则同步，否则不进行同步
        if (this.cachedSessionDataMap.isChanged()) {
            //map为空，则删除分布式session缓存数据
            if (this.cachedSessionDataMap.isEmpty()) {
                MemcachedSessionService.instance.removeSession(this.dsid);
            } else {
                //更新分布式session缓存数据
                MemcachedSessionService.instance.saveSession(this.dsid, this.cachedSessionDataMap);
            }
        }
    }
}
