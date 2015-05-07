package com.edwardsbean.timo.common;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by edwardsbean on 2015/5/6 0006.
 */
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    // 复用的对象单例
    private static JsonFactory jsonFactory = new JsonFactory();
    private static ObjectMapper defaultObjectMapper = createObjectMapper();

    /**
     * 创建一个自定义的JSON ObjectMapper
     */
    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
//        module.addSerializer(Paginator.class, new PaginatorJsonSerializer());
//        module.addDeserializer(Paginator.class, new PaginatorJsonDeserializer());
//        module.addSerializer(Money.class, new MoneyJsonSerializer());
//        module.addDeserializer(Money.class, new MoneyJsonDeserializer());
        objectMapper.registerModule(module);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        return objectMapper;
    }

    /**
     * 将对象转换为JSON字符串
     */
    public static <T> String transferToJson(T value) {
        if (value == null) {
            return null;
        }

        StringWriter sw = new StringWriter();
        JsonGenerator gen = null;
        try {
            gen = jsonFactory.createGenerator(sw);
            defaultObjectMapper.writeValue(gen, value);
            return sw.toString();
        } catch (IOException e) {
            logger.error("Json Transfer Exception Occurred!", e);
        } finally {
            if (gen != null) {
                try {
                    gen.close();
                } catch (IOException e) {
                    logger.warn("Exception occurred when closing JSON generator!", e);
                }
            }
        }
        return null;
    }

    /**
     * 将JSON字符串转换为指定对象
     */
    public static <T> T transferToObj(String jsonString, Class<T> valueType) {
        T value = null;
        try {
            value = defaultObjectMapper.readValue(jsonString, valueType);
        } catch (IOException e) {
            logger.error("Json Transfer Exception Occurred!", e);
        }
        return value;
    }
}
