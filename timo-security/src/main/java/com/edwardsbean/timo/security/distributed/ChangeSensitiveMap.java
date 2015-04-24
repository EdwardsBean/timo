package com.edwardsbean.timo.security.distributed;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对变化敏感的map
 */
public class ChangeSensitiveMap<K, V> extends ConcurrentHashMap<K, V> {
    private static final long serialVersionUID = 7133599295384027102L;
    /** 是否有修改过 */
    private volatile boolean  changed          = false;

    public ChangeSensitiveMap(Map<K, V> map) {
        super();
        if (map != null) {
            this.putAll(map);
        }
        changed = false;
    }

    @Override
    public V put(K key, V value) {
        V v = super.put(key, value);
        judgeChanged(v, value);
        return v;
    }

    public V putIfAbsent(K key, V value) {
        V v = super.putIfAbsent(key, value);
        judgeChanged(v, value);
        return v;
    }

    @Override
    public void clear() {
        if (!this.isEmpty()) {
            changed = true;
        }
        super.clear();
    }

    private void judgeChanged(Object oldValue, Object newValue) {
        if (ObjectUtils.notEqual(oldValue, newValue)) {
            changed = true;
        }
    }

    public boolean isChanged() {
        return changed;
    }
}
