package com.edwardsbean.timo.db.sharding.builder;

import com.edwardsbean.timo.db.sharding.strategy.ShardStrategy;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.util.ReflectionUtils;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author edwardsbean
 * @date 2015/4/27.
 */
public class ShardConfigParser {
    private static final Log log = LogFactory.getLog(ShardConfigParser.class);

    private static final String SHARD_CONFIG_DTD = "META-INF/sharding-config.dtd";
    private static final Map<String, String> DOC_TYPE_MAP = new HashMap<String, String>();
    static {
        DOC_TYPE_MAP.put(
                "http://shardbatis.googlecode.com/dtd/shardbatis-config.dtd"
                        .toLowerCase(), SHARD_CONFIG_DTD);
        DOC_TYPE_MAP.put("-//shardbatis.googlecode.com//DTD Shardbatis 2.0//EN"
                .toLowerCase(), SHARD_CONFIG_DTD);
    }

    /**
     * 解析xml配置文件并构建ShardConfigFactory
     * @param input
     * @return
     * @throws Exception
     */
    public ShardConfigHolder parse(InputStream input) throws Exception {
        final ShardConfigHolder configHolder = ShardConfigHolder
                .getInstance();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(true);
        spf.setNamespaceAware(true);
        SAXParser parser = spf.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        // 解析XML实现
        DefaultHandler handler = new DefaultHandler() {
            private String parentElement;
            private ShardStrategy currentStrategy;
            public void startElement(String uri, String localName,
                                     String qName, Attributes attributes) throws SAXException {

                if ("strategy".equals(qName)) {// 解析<strategy/>节点
                    // 解析<strategy tableName="xxx"/>
                    String table = attributes.getValue("tableName");
                    // 解析<strategy strategyClass="xxx"/>
                    String className = attributes.getValue("strategyClass");
                    try {
                        Class<?> clazz = Class.forName(className);
                        ShardStrategy strategy = (ShardStrategy) clazz
                                .newInstance();
                        configHolder.register(table, strategy);
                        currentStrategy = strategy;
                    } catch (ClassNotFoundException e) {
                        throw new SAXException(e);
                    } catch (InstantiationException e) {
                        throw new SAXException(e);
                    } catch (IllegalAccessException e) {
                        throw new SAXException(e);
                    }
                }

                if ("property".equals(qName)) {

                    String name = attributes.getValue("name");
                    String value = attributes.getValue("value");
                    try {
                        Field field = ReflectionUtils.findField(currentStrategy.getClass(), name);
                        field.setAccessible(true);
                        field.set(currentStrategy, getFieldValue(value, field.getType()));
                    } catch (Exception e) {

                        throw new SAXException(e);
                    }
                }

                if("ignoreList".equals(qName)||"parseList".equals(qName)){
                    parentElement=qName;
                }
            }

            private Object getFieldValue(String fieldValueStr, Class<?> fieldType) {

                Object fieldValue = null;
                if (Byte.class.equals(fieldType)) {

                    fieldValue = Byte.valueOf(fieldValueStr);
                } else if (Integer.class.equals(fieldType)) {

                    fieldValue = Integer.valueOf(fieldValueStr);
                } else if (Long.class.equals(fieldType)) {

                    fieldValue = Long.valueOf(fieldValueStr);
                }else {

                    fieldValue = fieldValueStr;
                }
                return fieldValue;
            }

            public void characters (char ch[], int start, int length)throws SAXException{
                if("ignoreList".equals(parentElement)){
                    configHolder.addIgnoreId(new String(ch, start, length).trim());
                }else if("parseList".equals(parentElement)){
                    configHolder.addParseId(new String(ch, start, length).trim());
                }

            }
            public void error(SAXParseException e) throws SAXException {
                throw e;
            }

            public InputSource resolveEntity(String publicId, String systemId)
                    throws IOException, SAXException {
                if (publicId != null)
                    publicId = publicId.toLowerCase();
                if (systemId != null)
                    systemId = systemId.toLowerCase();

                InputSource source = null;
                try {
                    String path = DOC_TYPE_MAP.get(publicId);
                    source = getInputSource(path, source);
                    if (source == null) {
                        path = DOC_TYPE_MAP.get(systemId);
                        source = getInputSource(path, source);
                    }
                } catch (Exception e) {
                    throw new SAXException(e.toString());
                }
                return source;
            }
        };

        reader.setContentHandler(handler);
        reader.setEntityResolver(handler);
        reader.setErrorHandler(handler);
        reader.parse(new InputSource(input));

        return configHolder;
    }

    private InputSource getInputSource(String path, InputSource source) {
        if (path != null) {
            InputStream in = null;
            try {
                in = Resources.getResourceAsStream(path);
                source = new InputSource(in);
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }
        return source;
    }

}
