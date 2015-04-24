package com.edwardsbean.timo.service.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.edwardsbean.timo.common.AppConfigUtil;
import com.edwardsbean.timo.common.Conventions;
import com.edwardsbean.timo.common.context.ServiceContextHolder;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/**
 * REST客户端辅助类
 *
 */
public abstract class RestClientSupport implements InitializingBean {
    private static final Logger logger                 = LoggerFactory
            .getLogger(RestClientSupport.class);

    private RestTemplate restTemplate;

//    /**
//     * 是否使用BNS来查找服务提供者IP和端口
//     */
//    private boolean             useBns                 = false;
//
//    /**
//     * 所需服务在BNS中配置的服务名
//     */
//    private String              bnsServiceName;

    /**
     * 提供服务的服务器地址及端口：
     *
     * 例如：http://xxxx:8081
     */
    private String              serviceProvider;

    // 以下参数用于设置RestTemplate

    /**
     * 连接超时，默认5s
     */
    private int                 connectTimeout         = 5000;
    /**
     * 读取数据超时，默认10s
     */
    private int                 readTimeout            = 10000;
    /**
     * 最大连接数，默认100
     */
    private int                 maxConnections         = 100;
    /**
     * 每个Route的最大连接数，默认20
     */
    private int                 maxConnectionsPerRoute = 20;
    /**
     * 默认编码格式
     */
    private String              charset                = "UTF-8";

    /**
     * 是否开启压缩功能
     */
    private boolean             compress               = true;

    /**
     * 1、检查serviceProvider不能为空
     * 2、如果没有指定restTemplate，创建一个默认的
     */
    public void init() {
        if (this.restTemplate == null) {
            AdvancedRestTemplate restTemplate = new AdvancedRestTemplate();
            restTemplate.setCharset(charset);
            restTemplate.setCompress(compress);
            restTemplate.setConnectTimeout(connectTimeout);
            restTemplate.setReadTimeout(readTimeout);
            restTemplate.setMaxConnections(maxConnections);
            restTemplate.setMaxConnectionsPerRoute(maxConnectionsPerRoute);
            restTemplate.init();
            this.restTemplate = restTemplate;
        }
    }

    /**
     * @see #init()
     */
    @Override
    public void afterPropertiesSet() {
        init();
    }

    /**
     * 通过POST的方式调用远端，并返回指定类型的对象
     *
     * @param serviceUrl 服务的URL
     * @param param 请求的参数
     * @param responseClass 响应的类型
     * @param uriVariables 用来生成实际URL的参数，可为空
     * @return 远程请求返回的结果
     */
    protected <T, P> T postForObject(String serviceUrl, P param, Class<T> responseClass,
                                     Object... uriVariables) {
        Assert.hasText(serviceProvider, "serviceProvider MUST have text");
        Assert.hasText(serviceUrl, "serviceUrl MUST have text");
        Assert.notNull(responseClass, "responseClass must NOT be null");

        HttpEntity<P> entity = createHttpEntity(param);
        URI uri = getUri(serviceUrl, uriVariables);
        logger.debug("准备向{}发起POST请求获取{}类型对象结果", uri, responseClass.getSimpleName());
        T result = null;
        try {
            result = restTemplate.postForObject(uri, entity, responseClass);
        } catch (ResourceAccessException e) {
//            if (useBns) {
//                bnsClient.addInstanceFailureCount(bnsServiceName, uri.getHost());
//            }
            throw e;
        }
        return result;
    }

    /**
     * 通过GET的方式调用远端，并返回指定类型的对象
     *
     * @param serviceUrl 服务的URL
     * @param responseClass 响应的类型
     * @param uriVariables 用来生成实际URL的参数，可为空
     * @return 远程请求返回的结果
     */
    protected <T> T getForObject(String serviceUrl, Class<T> responseClass, Object... uriVariables) {
        return exchangeForObject(serviceUrl, HttpMethod.GET, responseClass, uriVariables);
    }

    /**
     * 通过DELETE的方式调用远端，并返回指定类型的对象
     *
     * @param serviceUrl 服务的URL
     * @param responseClass 响应的类型
     * @param uriVariables 用来生成实际URL的参数，可为空
     * @return 远程请求返回的结果
     */
    protected <T> T deleteForObject(String serviceUrl, Class<T> responseClass, Object... uriVariables) {
        return exchangeForObject(serviceUrl, HttpMethod.DELETE, responseClass, uriVariables);
    }

    /**
     * 提供基础HTTP调用获取返回对象的方法，封装了exchange方法
     */
    private <T> T exchangeForObject(String serviceUrl, HttpMethod method, Class<T> responseClass,
                                    Object... uriVariables) {
        Assert.hasText(serviceProvider, "serviceProvider MUST have text");
        Assert.hasText(serviceUrl, "serviceUrl MUST have text");
        Assert.notNull(responseClass, "responseClass must NOT be null");

        HttpEntity<?> entity = createHttpEntity();
        URI uri = getUri(serviceUrl, uriVariables);
        logger.info("准备向{}发起{}请求获取{}类型对象结果", uri, method, responseClass.getSimpleName());

        T result;
        try {
            ResponseEntity<T> response = restTemplate.exchange(uri, method, entity, responseClass);
            if (response != null && response.getStatusCode() != null &&
                    response.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL) {
                logger.warn("请求响应码不成功，请关注！响应为{}", response);
            }
            result = response.getBody();
        } catch (ResourceAccessException e) {
//            if (useBns) {
//                bnsClient.addInstanceFailureCount(bnsServiceName, uri.getHost());
//            }
            throw e;
        }
        return result;
    }

    /**
     * 获得完整的REST服务URL地址
     * 如果开启了BNS支持，则从BNS获取地址，如果BNS出现错误，则降级为使用配置项
     *
     * @param serviceUrl 传入的URL片段
     * @param urlVariables 要扩展到URL里的参数值，如果serviceUrl里没有{}，但是又提供了1个参数，则用其中的属性智能扩展URL
     */
    private URI getUri(String serviceUrl, Object... urlVariables) {
        if (!StringUtils.contains(serviceUrl, "{") && !StringUtils.contains(serviceUrl, "}")
                && urlVariables.length == 1) {
            Map<String, Object> params = convertBeanToMap(urlVariables[0]);
            return getUri(serviceUrl, params);
        }

        String url = serviceUrl;
        String provider = this.serviceProvider;
        if (!StringUtils.startsWith(serviceUrl, "/")) {
            url = "/" + serviceUrl;
        }
        return new UriTemplate(provider + url).expand(urlVariables);
    }

    /**
     * 获得完整的REST服务URL地址
     * 如果开启了BNS支持，则从BNS获取地址，如果BNS出现错误，则降级为使用配置项
     *
     * @param serviceUrl 传入的URL片段
     * @param urlVariables 要扩展到URL里的参数值，如果serviceUrl里没有{}，使用Map中的Key智能扩展URL
     */
    private URI getUri(String serviceUrl, Map<String, ?> urlVariables) {
        String url = serviceUrl;
        String provider = this.serviceProvider;
        if (!StringUtils.startsWith(serviceUrl, "/")) {
            url = "/" + serviceUrl;
        }

        if (!StringUtils.contains(url, "{") && !StringUtils.contains(url, "}")) {
            String qs = generateQueryStringFromMap(urlVariables);
            if (StringUtils.isNotBlank(qs) && StringUtils.contains(url, "?")) {
                url = url + "&" + qs;
            } else {
                url = url + "?" + qs;
            }
        }
        return new UriTemplate(provider + url).expand(urlVariables);
    }


    /**
     * 将一个Bean转换为Map，key是属性名，value是属性值
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertBeanToMap(Object bean) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (bean == null || bean instanceof String || bean.getClass().isPrimitive()) {
            return map;
        }
        if (bean instanceof Map) {//map
            for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) bean).entrySet()) {
                if (entry.getKey() instanceof String) {
                    map.put(entry.getKey().toString(), entry.getValue());
                } else {
                    String exceptionMessage = "无法将Map[" + bean + "]转换为Map<String, ?>";
                    logger.warn(exceptionMessage);
                    throw new IllegalArgumentException(exceptionMessage);
                }
            }
        } else {//bean
            try {
                map = PropertyUtils.describe(bean);
            } catch (Exception e) {
                String exceptionMessage = "无法将Bean[" + bean + "]转换为Map<String, ?>";
                logger.warn(exceptionMessage, e);
                throw new IllegalArgumentException(exceptionMessage, e);
            }
        }
        return map;
    }

    /**
     * 根据Map<String, ?>来生成URL后的QueryString
     * 过滤了key为class的项
     * 过滤了value为数组、Map、各种集合类以及null的项
     *
     * @param map 其中的Key将作为QueryString的Key
     * @return key1={key1}&key2={key2}形式的字符串
     */
    private String generateQueryStringFromMap(Map<String, ?> map) {
        List<String> entriesToBeRemoved = new ArrayList<String>();
        entriesToBeRemoved.add("class");
        for (String key : map.keySet()) {
            if (map.get(key) == null) {
                entriesToBeRemoved.add(key);
                continue;
            }
            Class<?> clazz = map.get(key).getClass();
            if (clazz.isAssignableFrom(Collection.class) || clazz.isAssignableFrom(Map.class)
                    || clazz.isArray()) {
                entriesToBeRemoved.add(key);
            }
        }
        for (String key : entriesToBeRemoved) {
            map.remove(key);
        }

        StringBuilder queryString = new StringBuilder();
        for (String key : map.keySet()) {
            queryString.append(key).append("=").append("{").append(key).append("}&");
        }
        return StringUtils.removeEnd(queryString.toString(), "&");
    }

    /**
     * 创建HttpEntity，主要是增加一些HTTP头
     *
     * @param param 请求正文对象
     * @return 头+正文
     */
    private <P> HttpEntity<P> createHttpEntity(P param) {
        return new HttpEntity<P>(param, createHttpHeaders());
    }

    /**
     * 创建HttpEntity，主要是增加一些HTTP头
     *
     * @return 头+正文
     */
    private HttpEntity<?> createHttpEntity() {
        return new HttpEntity<Object>(createHttpHeaders());
    }

    /**
     * 创建远程调用使用的HTTP头
     */
    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> context = ServiceContextHolder.get();
        for (String key : context.keySet()) {
            headers.set(key, context.get(key));
        }
        headers.remove(Conventions.REAL_IP_HEADER);
        headers.set(Conventions.SRC_SYS_HEADER, AppConfigUtil.getAppName());
        return headers;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }


    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
