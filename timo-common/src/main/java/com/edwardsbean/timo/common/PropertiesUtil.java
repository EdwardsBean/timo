package com.edwardsbean.timo.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * Properties辅助类
 *
 */
public class PropertiesUtil {
    /**
     * 命令行工具，将两个Properties合并后输出到指定文件中。
     *
     * @param args 共三个参数，源文件1 源文件2 目标文件
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out
                    .println("Merge two properties files to the dest file.\nArguments: src1 src2 dest");
            System.exit(-1);
        }
        mergePropertiesFiles(args[0], args[1], args[2]);
    }

    /**
     * 将两个Properties文件合并为一个，如有重复属性，以src2中的为准
     */
    public static void mergePropertiesFiles(String src1, String src2, String dest)
            throws IOException {
        Properties p1 = loadFromFile(src1);
        Properties p2 = loadFromFile(src2);
        Properties merged = mergeProperties(p1, p2);
        writePropertiesToFile(merged, dest);
    }

    /**
     * 合并两个Properties，如存在同名属性，以p2中的值为准，如果某个Properties为null，将会被忽略
     *
     * @return 返回一个新的Properties对象，保证非null
     */
    public static Properties mergeProperties(Properties p1, Properties p2) {
        Properties merged = new Properties();
        if (p1 != null) {
            merged.putAll(p1);
        }
        if (p2 != null) {
            merged.putAll(p2);
        }
        return merged;
    }

    /**
     * 将Properties写入文件之中
     *
     * @param properties 要写到文件的属性，不能为null
     * @param file 目标文件，不能为空
     */
    public static void writePropertiesToFile(Properties properties, String file) throws IOException {
        if (properties == null || StringUtils.isBlank(file)) {
            throw new IllegalArgumentException("Parameters can't be blank.");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            properties.store(fos, "");
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * 从文件中加载属性文件
     *
     * @param file 要读取的文件
     */
    public static Properties loadFromFile(String file) throws IOException {
        if (StringUtils.isBlank(file)) {
            throw new IllegalArgumentException("file parameter can't be blank.");
        }
        Properties p = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            p.load(fis);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return p;
    }

    /**
     * 从当前Classpath中加载Properties文件
     */
    public static Properties loadFromClasspath(String file) throws IOException {
        if (StringUtils.isBlank(file)) {
            throw new IllegalArgumentException("file parameter can't be blank.");
        }
        Properties p = new Properties();
        InputStream in = null;
        try {
            in = PropertiesUtil.class.getClassLoader().getResourceAsStream(file);
            p.load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return p;
    }
}