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
 * 定制的MappingJackson2HttpMessageConverter，支持GBK等其他编码,支持Header注入解析后的Json对象
 * @author edwardsbean
 * @date 2015/5/2 0002.
 */
public class CustomMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    private ObjectMapper objectMapper = JsonUtil.createObjectMapper();
    private String charset = "UTF-8";
    private List<MediaType> additionalMediaTypes = new ArrayList<MediaType>();

    /**
     * 添加UTF-8和指定编码支持
     */
    public CustomMappingJackson2HttpMessageConverter(String charset) {
        if (!Charset.isSupported(charset)) {
            throw new IllegalCharsetNameException("The given charset " + charset + " is not supported!!");
        }
        if (StringUtils.equalsIgnoreCase(charset, this.charset)) {
            setSupportedMediaTypes(Arrays.asList(new MediaType("application", "json", DEFAULT_CHARSET),
                    new MediaType("application", "*+json", DEFAULT_CHARSET)));
        } else {
            this.charset = charset;
            setSupportedMediaTypes(Arrays.asList(new MediaType("application", "json", Charset.forName(charset)),
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

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        JavaType javaType = getJavaType(clazz, null);
        return readJavaType(javaType, inputMessage);
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        outputMessage.getBody();
        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
        JsonGenerator jsonGenerator = this.objectMapper.getFactory().createGenerator(baos, encoding);

        if (this.objectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jsonGenerator.useDefaultPrettyPrinter();
        }

        try {
            this.objectMapper.writeValue(jsonGenerator, object);
            outputMessage.getBody().write(new String(baos.toByteArray(), DEFAULT_CHARSET).getBytes(this.charset));
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
        try {
            InputStream is = inputMessage.getBody();
            MediaType mediaType = inputMessage.getHeaders().getContentType();

            // 如果明确指定了UTF-8，使用默认编码
            if (mediaType.getCharSet() != null &&
                    StringUtils.equals(DEFAULT_CHARSET.name(), mediaType.getCharSet().name())) {
                return this.objectMapper.readValue(is, javaType);
            } else {
                byte[] contents = StreamUtils.copyToByteArray(is);
                byte[] converted = new String(contents, this.charset).getBytes(DEFAULT_CHARSET);
                ByteArrayInputStream bais = new ByteArrayInputStream(converted);
                return this.objectMapper.readValue(bais, javaType);
            }
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    /**
     * 追加额外支持的MediaType
     */
    public void setAdditionalMediaTypes(List<MediaType> additionalMediaTypes) {
        this.additionalMediaTypes = additionalMediaTypes;
        if (additionalMediaTypes != null && !additionalMediaTypes.isEmpty()) {
            List<MediaType> mediaTypes = new ArrayList<MediaType>(getSupportedMediaTypes());
            for (MediaType t : additionalMediaTypes) {
                mediaTypes.add(t);
            }
            setSupportedMediaTypes(mediaTypes);
        }
    }

    public String getCharset() {
        return charset;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}

