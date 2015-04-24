package com.edwardsbean.timo.service.client;

import com.edwardsbean.timo.common.pagination.Paginator;
import com.edwardsbean.timo.service.client.serialization.PaginatorJsonDeserializer;
import com.edwardsbean.timo.service.client.serialization.PaginatorJsonSerializer;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Arrays;


/**
 * 定制的MappingJackson2HttpMessageConverter，除了UTF-8，还可以支持GBK等其他编码
 *
 */
public class CustomMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    private ObjectMapper objectMapper = new ObjectMapper();
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
        configObjectmapper();
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        JavaType javaType = getJavaType(type, contextClass);
        return readJavaType(javaType, inputMessage);
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
        JsonGenerator jsonGenerator = this.objectMapper.getFactory()
                .createGenerator(baos, encoding);

        if (this.objectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jsonGenerator.useDefaultPrettyPrinter();
        }

        try {
            this.objectMapper.writeValue(jsonGenerator, object);
            outputMessage.getBody().write(
                    new String(baos.toByteArray(), DEFAULT_CHARSET).getBytes(this.charset));
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(),
                    ex);
        }
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
        try {
            InputStream is = inputMessage.getBody();
            MediaType mediaType = inputMessage.getHeaders().getContentType();

            // 如果明确指定了UTF-8，使用默认编码
            if (mediaType.getCharSet() != null && StringUtils
                    .equals(DEFAULT_CHARSET.name(), mediaType.getCharSet().name())) {
                return this.objectMapper.readValue(is, javaType);
            } else {
                byte[] contents = StreamUtils.copyToByteArray(is);
                byte[] converted = new String(contents, this.charset).getBytes(DEFAULT_CHARSET);
                ByteArrayInputStream bais = new ByteArrayInputStream(converted);
                return this.objectMapper.readValue(bais, javaType);
            }
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(),
                    ex);
        }
    }

    /**
     * 添加对Money和Paginator的序列化支持
     */
    private void configObjectmapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Paginator.class, new PaginatorJsonSerializer());
        module.addDeserializer(Paginator.class, new PaginatorJsonDeserializer());
        objectMapper.registerModule(module);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true) ;
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
