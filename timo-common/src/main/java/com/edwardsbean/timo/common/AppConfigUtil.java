package com.edwardsbean.timo.common;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用配置辅助工具
 *
 */
public class AppConfigUtil {
    private static final Logger logger                 = LoggerFactory
            .getLogger(AppConfigUtil.class);
    private static final String APP_NAME_KEY           = "app.name";
    private static final String resourcePathSuffix     = "-app-config.properties";

    /**
     * 如果在配置文件中没有找到指定KEY，是否查找系统环境变量，还是忽略系统环境变量
     */
    private static boolean      ignoreSystemProperties = true;

    private static Properties   properties             = new Properties();

    static {
        String appName = System.getProperty(APP_NAME_KEY);
        String configFile = appName + resourcePathSuffix;
        try {
            properties.putAll(PropertiesUtil.loadFromClasspath(configFile));
        } catch (Exception e) {
            logger.error("加载系统配置文件" + configFile + "时发生异常！！！", e);
        }
    }

    /**
     * 返回系统名
     */
    public static String getAppName() {
        return System.getProperty(APP_NAME_KEY);
    }

    /**
     * 从配置文件中加载指定变量的值。
     * 如果没有找到，根据ignoreSystemProperties的设置，查找系统环境变量
     */
    public static String getProperty(String key) {
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        } else if (!ignoreSystemProperties) {
            return System.getProperty(key);
        }
        return null;
    }

    /**
     * 判断是否运行在生产模式下
     */
    public static boolean isProdMode() {
        String mode = getProperty(Conventions.RUN_MODE_PROPERTY_KEY);
        return StringUtils.equalsIgnoreCase(mode, Conventions.MODE_PROD);
    }

    public static void enableSystemProperties() {
        ignoreSystemProperties = false;
    }

    public static void disableSystemProperties() {
        ignoreSystemProperties = true;
    }
}