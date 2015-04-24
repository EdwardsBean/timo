package com.edwardsbean.timo.service.client;


import java.util.ArrayList;
import java.util.List;


import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;

/**
 * 增强的RestTemplate辅助类，支持连接池和压缩功能
 *
 */
public class AdvancedRestTemplate extends RestTemplate {
    /**
     * 连接超时，默认5s
     */
    private int     connectTimeout         = 5000;
    /**
     * 读取数据超时，默认10s
     */
    private int     readTimeout            = 10000;
    /**
     * 最大连接数，默认100
     */
    private int     maxConnections         = 100;
    /**
     * 每个Route的最大连接数，默认20
     */
    private int     maxConnectionsPerRoute = 20;
    /**
     * 编码，默认UTF-8
     */
    private String  charset                = "UTF-8";
    /**
     * 是否开启压缩功能，默认开启
     */
    private boolean compress               = true;

    /**
     * 初始化，主要是设置了MessageConverter列表和RequestFactory，对它做了定制
     */
    @SuppressWarnings("rawtypes")
    public void init() {
        Assert.isTrue(
                ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper",
                        RestTemplate.class.getClassLoader())
                        && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator",
                        RestTemplate.class.getClassLoader()), "JacksonJson2 NOT found!!");

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();

        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter());
        messageConverters.add(new CustomAllEncompassingFormHttpMessageConverter(charset));
        messageConverters.add(new CustomMappingJackson2HttpMessageConverter(charset));

        super.setMessageConverters(messageConverters);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(
                schemeRegistry);
        connectionManager.setMaxTotal(maxConnections);
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);

        HttpClient httpClient = new DefaultHttpClient(connectionManager);
        if (compress) {
            httpClient = new DecompressingHttpClient(httpClient);
        }

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
                httpClient);
        requestFactory.setReadTimeout(readTimeout);
        requestFactory.setConnectTimeout(connectTimeout);
        super.setRequestFactory(requestFactory);
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxConnectionsPerRoute() {
        return maxConnectionsPerRoute;
    }

    public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }
}
