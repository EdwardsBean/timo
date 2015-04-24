package com.edwardsbean.timo.service.client;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

/**
 * 自定义的AllEncompassingFormHttpMessageConverter，对于其中的JSON格式内容，支持UTF-8以外的字符编码
 *
 */
public class CustomAllEncompassingFormHttpMessageConverter extends FormHttpMessageConverter {
    private String charset = "UTF-8";

    @SuppressWarnings("rawtypes")
    public CustomAllEncompassingFormHttpMessageConverter(String charset) {
        if (!Charset.isSupported(charset)) {
            throw new IllegalCharsetNameException("The given charset " + charset
                    + " is not supported!!");
        }
        addPartConverter(new SourceHttpMessageConverter());
        addPartConverter(new CustomMappingJackson2HttpMessageConverter(charset));
        super.setCharset(Charset.forName(charset));
        this.charset = charset;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }
}
