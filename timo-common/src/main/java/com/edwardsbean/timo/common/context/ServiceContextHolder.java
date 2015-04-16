package com.edwardsbean.timo.common.context;

import com.edwardsbean.timo.common.Conventions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程服务上下文，存放需要通过远程调用传递的非业务数据
 *
 */
public class ServiceContextHolder {
    private static final ThreadLocal<Map<String, String>> CONTEXT = new ThreadLocal<Map<String,
            String>>();

    /**
     * 清理旧的内容
     */
    public static void init() {
        CONTEXT.remove();
        CONTEXT.set(new ConcurrentHashMap<String, String>());
        fillCommonContents(CONTEXT.get());
    }

    /**
     * 设置键值内容，两者都不能为空白和null
     */
    public static boolean put(String key, String value) {
        boolean flag = false;
        if (CONTEXT.get() == null) {
            init();
        }
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            CONTEXT.get().put(key, value);
            flag = true;
        }
        return flag;
    }

    /**
     * 获得目前的上下文，其中会填充一些公共内容
     */
    public static Map<String, String> get() {
        Map<String, String> context = CONTEXT.get();
        if (context == null) {
            context = new HashMap<String, String>();
            fillCommonContents(context);
        }
        return context;
    }

    /**
     * 获得上下文中的特定参数
     *
     * @param key 要获得的参数名
     * @return 如果上下文为null或者参数名为空，直接返回null
     */
    public static String get(String key) {
        if (CONTEXT.get() != null && StringUtils.isNotBlank(key)) {
            return CONTEXT.get().get(key);
        }
        return null;
    }

    /**
     * 从LOG的上下文里取出LOGID和请求来源系统名
     */
    private static void fillCommonContents(Map<String, String> context) {
        String mdc = MDC.get(Conventions.CTX_LOG_ID_MDC);
        if (StringUtils.isNotBlank(mdc)) {
            context.put(Conventions.LOG_ID_HEADER, mdc);
        }
        mdc = MDC.get(Conventions.CTX_SRC_SYS_MDC);
        if (StringUtils.isNotBlank(mdc)) {
            context.put(Conventions.SRC_SYS_HEADER, mdc);
        }
    }
}
