package com.edwardsbean.timo.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于保存Web摘要日志参数的辅助类
 *
 */
public class WebDigestLogAdditionalInfoUtil {
    private static final ThreadLocal<List<Object>> PARAMETERS = new ThreadLocal<List<Object>>();
    private static final ThreadLocal<List<Object>> RESULTS    = new ThreadLocal<List<Object>>();

    /**
     * 清空当前内容
     */
    public static void clear() {
        PARAMETERS.remove();
        PARAMETERS.set(new ArrayList<Object>());
        RESULTS.remove();
        RESULTS.set(new ArrayList<Object>());
    }

    /**
     * 向上下文中增加参数
     */
    public static void addParameters(Object... parameters) {
        putIntoThreadLocal(PARAMETERS, parameters);
    }

    /**
     * 获得上下文中的参数
     *
     * @return 始终不会为null
     */
    public static List<Object> getParameters() {
        return getFromThreadLocal(PARAMETERS);
    }

    /**
     * 向上下文中增加结果
     */
    public static void addResults(Object... results) {
        putIntoThreadLocal(RESULTS, results);
    }

    /**
     * 获得上下文中的结果
     *
     * @return 始终不会为null
     */
    public static List<Object> getResults() {
        return getFromThreadLocal(RESULTS);
    }

    /**
     * 从ThreadLocal对象中取数据
     */
    private static List<Object> getFromThreadLocal(ThreadLocal<List<Object>> holder) {
        if (holder.get() != null) {
            return holder.get();
        } else {
            return new ArrayList<Object>();
        }
    }

    /**
     * 向ThreadLocal对象中填写数据
     */
    private static void putIntoThreadLocal(ThreadLocal<List<Object>> holder, Object... contents) {
        if (holder.get() != null) {
            holder.get().addAll(Arrays.asList(contents));
        } else {
            holder.set(new ArrayList<Object>(Arrays.asList(contents)));
        }
    }
}
