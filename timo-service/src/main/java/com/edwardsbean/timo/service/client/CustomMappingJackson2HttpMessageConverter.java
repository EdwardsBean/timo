package com.edwardsbean.timo.service.client;

import com.edwardsbean.timo.common.Conventions;
import com.edwardsbean.timo.common.JsonUtil;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 定制的MappingJackson2HttpMessageConverter，支持Header注入解析后的Json对象
 * @author edwardsbean
 * @date 2015/5/2 0002.
 */
public class CustomMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    private String       charset      = "UTF-8";

    /**
     * 添加UTF-8和指定编码支持
     */
    public CustomMappingJackson2HttpMessageConverter(String charset) {
        if (!Charset.isSupported(charset)) {
            throw new IllegalCharsetNameException("The given charset " + charset
                    + " is not supported!!");
        }
        if (StringUtils.equalsIgnoreCase(charset, this.charset)) {
            setSupportedMediaTypes(Arrays.asList(new MediaType("application", "json",
                    DEFAULT_CHARSET), new MediaType("application", "*+json", DEFAULT_CHARSET)));
        } else {
            this.charset = charset;
            setSupportedMediaTypes(
                    Arrays.asList(new MediaType("application", "json", Charset.forName(charset)),
                            new MediaType("application", "*+json", Charset.forName(charset)),
                            new MediaType("application", "json", DEFAULT_CHARSET),
                            new MediaType("application", "*+json", DEFAULT_CHARSET)));
        }
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        JavaType javaType = getJavaType(type, contextClass);
        Class paramClass = javaType.getRawClass();
        Object object = readJavaType(javaType, inputMessage);
        //TODO:添加更多Header转换
        String divideVersion = inputMessage.getHeaders().getFirst(Conventions.DIVIDE_VERSION);
        String PID = inputMessage.getHeaders().getFirst(Conventions.PID);
        String supPhone = inputMessage.getHeaders().getFirst(Conventions.SUP_PHONE);
        String supFirm = inputMessage.getHeaders().getFirst(Conventions.SUP_FIRM);
        String IMEI = inputMessage.getHeaders().getFirst(Conventions.IMEI);
        String IMSI = inputMessage.getHeaders().getFirst(Conventions.IMSI);
        try {
            setField(object, paramClass, "divideVersion", divideVersion);
            setField(object, paramClass, "pid", PID);
            setField(object, paramClass, "supFirm", supFirm);
            setField(object, paramClass, "supPhone", supPhone);
            setField(object, paramClass, "imei", IMEI);
            setField(object, paramClass, "imsi", IMSI);
        } catch (Exception e) {
            throw new HttpMessageNotReadableException("将Header注入Json对象失败", e);
        }

        return object;
    }

    private void setField(Object object, Class<?> clazz, String fieldName, String value) {
        if (value != null) {
            Field field = ReflectionUtils.findField(clazz, fieldName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, object, value);
            }
        }
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
        try {
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        }
        catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

}

