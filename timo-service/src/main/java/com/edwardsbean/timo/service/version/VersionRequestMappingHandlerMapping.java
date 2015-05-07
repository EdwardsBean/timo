package com.edwardsbean.timo.service.version;

import com.edwardsbean.timo.common.Conventions;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 自动版本路由支持
 * @author edwardsbean
 * @date 2015/5/2 0002.
 */
public class VersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        VersionSupport versionSupport = AnnotationUtils.findAnnotation(method, VersionSupport.class);
        return (versionSupport != null) ? new VersionRequestCondition(versionSupport.value()) : null;
    }

    @Override
    protected HandlerMethod handleNoMatch(Set<RequestMappingInfo> requestMappingInfos, String lookupPath, HttpServletRequest request) throws ServletException {
        Set<String> allowedMethods = new LinkedHashSet<String>(4);

        //url匹配的
        Set<RequestMappingInfo> patternMatches = new HashSet<RequestMappingInfo>();
        //url匹配的,且http method匹配的
        Set<RequestMappingInfo> patternAndMethodMatches = new HashSet<RequestMappingInfo>();


        for (RequestMappingInfo info : requestMappingInfos) {
            //url是否匹配
            if (info.getPatternsCondition().getMatchingCondition(request) != null) {
                patternMatches.add(info);
                //接着看HTTP method是否匹配
                if (info.getMethodsCondition().getMatchingCondition(request) != null) {
                    patternAndMethodMatches.add(info);
                }
                else {
                    for (RequestMethod method : info.getMethodsCondition().getMethods()) {
                        allowedMethods.add(method.name());
                    }
                }
            }
        }

        //是否有url匹配的，没有肯定就404了
        if (patternMatches.isEmpty()) {
            return null;
        }
        else if (patternAndMethodMatches.isEmpty() && !allowedMethods.isEmpty()) {
            throw new HttpRequestMethodNotSupportedException(request.getMethod(), allowedMethods);
        }

        //保存所有不匹配的信息
        Set<MediaType> consumableMediaTypes;
        Set<MediaType> producibleMediaTypes;
        Set<String> paramConditions;

        //url匹配的控制器,进一步筛选不匹配信息
        if (patternAndMethodMatches.isEmpty()) {
            consumableMediaTypes = getConsumableMediaTypes(request, patternMatches);
            producibleMediaTypes = getProducibleMediaTypes(request, patternMatches);
            paramConditions = getRequestParams(request, patternMatches);
        }
        else {
            consumableMediaTypes = getConsumableMediaTypes(request, patternAndMethodMatches);
            producibleMediaTypes = getProducibleMediaTypes(request, patternAndMethodMatches);
            paramConditions = getRequestParams(request, patternAndMethodMatches);
        }

        //consume不匹配的话，则报错
        if (!consumableMediaTypes.isEmpty()) {
            MediaType contentType = null;
            if (StringUtils.hasLength(request.getContentType())) {
                try {
                    contentType = MediaType.parseMediaType(request.getContentType());
                }
                catch (InvalidMediaTypeException ex) {
                    throw new HttpMediaTypeNotSupportedException(ex.getMessage());
                }
            }
            throw new HttpMediaTypeNotSupportedException(contentType, new ArrayList<MediaType>(consumableMediaTypes));
        }
        //produce不匹配的话，则报错
        else if (!producibleMediaTypes.isEmpty()) {
            throw new HttpMediaTypeNotAcceptableException(new ArrayList<MediaType>(producibleMediaTypes));
        }
        //请求参数param不匹配的话，则报错
        else if (!CollectionUtils.isEmpty(paramConditions)) {
            String[] params = paramConditions.toArray(new String[paramConditions.size()]);
            throw new UnsatisfiedServletRequestParameterException(params, request.getParameterMap());

        }
        else {
            //自动选择最适合的版本

            //request中没有带version头,在lookupHandlerMethod时没找到不带版本号的controller
            //所以自动在带版本号的controller中选取最新版本controller
            String clientVersion = request.getHeader(Conventions.VERSION);
            List<VersionInfo> versions = new ArrayList<VersionInfo>();
            if (clientVersion == null) {
                for (RequestMappingInfo info : patternAndMethodMatches) {
                    if (info.getCustomCondition() != null) {
                        VersionRequestCondition condition = (VersionRequestCondition) info.getCustomCondition();
                        //取出版本号对应的MappingInfo
                        String version = condition.getVersionExpression().getVersion();
                        versions.add(new VersionInfo(version, getHandlerMethods().get(info)));
                    }
                }
            }
            //对MappingInfo根据版本号排序，取版本号最大，并拿到methodHandler
            if (!versions.isEmpty()) {
                Collections.sort(versions, new VersionInfoComparator());
                return versions.get(0).handlerMethod;
            }
            //return methodHandler
            return super.handleNoMatch(requestMappingInfos, lookupPath, request);
        }
    }


    private class VersionInfo {
        private final String version;
        private final HandlerMethod handlerMethod;

        public VersionInfo(String version, HandlerMethod handlerMethod) {
            this.version = version;
            this.handlerMethod = handlerMethod;
        }

        @Override
        public String toString() {
            return version;
        }
    }

    private class VersionInfoComparator implements Comparator<VersionInfo> {
        @Override
        public int compare(VersionInfo o1, VersionInfo o2) {
            return o2.version.compareTo(o1.version);
        }
    }


    private Set<MediaType> getConsumableMediaTypes(HttpServletRequest request, Set<RequestMappingInfo> partialMatches) {
        Set<MediaType> result = new HashSet<MediaType>();
        Set<RequestMappingInfo> removes = new HashSet<RequestMappingInfo>();
        for (RequestMappingInfo partialMatch : partialMatches) {
            if (partialMatch.getConsumesCondition().getMatchingCondition(request) == null) {
                result.addAll(partialMatch.getConsumesCondition().getConsumableMediaTypes());
                removes.add(partialMatch);
            }
        }
        partialMatches.removeAll(removes);
        return result;
    }

    private Set<MediaType> getProducibleMediaTypes(HttpServletRequest request, Set<RequestMappingInfo> partialMatches) {
        Set<MediaType> result = new HashSet<MediaType>();
        Set<RequestMappingInfo> removes = new HashSet<RequestMappingInfo>();
        for (RequestMappingInfo partialMatch : partialMatches) {
            if (partialMatch.getProducesCondition().getMatchingCondition(request) == null) {
                result.addAll(partialMatch.getProducesCondition().getProducibleMediaTypes());
                removes.add(partialMatch);
            }
        }
        partialMatches.removeAll(removes);
        return result;
    }

    private Set<String> getRequestParams(HttpServletRequest request, Set<RequestMappingInfo> partialMatches) {
        Set<RequestMappingInfo> removes = new HashSet<RequestMappingInfo>();
        Set<String> expressions = new HashSet<String>();
        for (RequestMappingInfo partialMatch : partialMatches) {
            ParamsRequestCondition condition = partialMatch.getParamsCondition();
            if (!CollectionUtils.isEmpty(condition.getExpressions()) && (condition.getMatchingCondition(request) == null)) {
                for (NameValueExpression<String> expr : condition.getExpressions()) {
                    expressions.add(expr.toString());
                }
                removes.add(partialMatch);
            }
        }
        partialMatches.removeAll(removes);
        return expressions;
    }
}
