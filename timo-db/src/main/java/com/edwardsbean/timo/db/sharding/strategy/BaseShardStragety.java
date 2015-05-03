package com.edwardsbean.timo.db.sharding.strategy;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author edwardsbean
 * @date 2015/4/27.
 */
public class BaseShardStragety {

    private static final Log logger = LogFactory.getLog(BaseShardStragety.class);
    protected String targetTableNamePattern;
    protected String keyFieldName;
    protected Long divisor;

    public BaseShardStragety() {
        super();
    }

    protected Object getKeyFieldValue(Object params) {
        Object value = null;
        if (params instanceof Map) {

            value = ((Map) params).get(keyFieldName);
        } else {

            try {
                Field field = getField(params.getClass(), keyFieldName);
                field.setAccessible(true);
                value = field.get(params);
            } catch (Exception e) {
                logger.error("GET_KEY_FIELD_VALUE_ERROR", e);
                throw new RuntimeException("GET_KEY_FIELD_VALUE_ERROR", e);
            }
        }
        return value;
    }

    private Field getField(Class clazz, String fieldName) throws Exception {

        Field ret = ReflectionUtils.findField(clazz, fieldName);
        if (null != ret) {

            return ret;
        }

        if (clazz.equals(Object.class)) {

            return null;
        }
        return getField(clazz.getSuperclass(), fieldName);
    }

    public String getTargetTableNamePattern() {
        return targetTableNamePattern;
    }

    public void setTargetTableNamePattern(String targetTableNamePattern) {
        this.targetTableNamePattern = targetTableNamePattern;
    }

    public String getKeyFieldName() {
        return keyFieldName;
    }

    public void setKeyFieldName(String keyFieldName) {
        this.keyFieldName = keyFieldName;
    }

    public Long getDivisor() {
        return divisor;
    }

    public void setDivisor(Long divisor) {
        this.divisor = divisor;
    }
}
