package com.edwardsbean.timo.service.version;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @author: edwardsbean
 * @date: 2015/5/2 0002.
 */
public class VersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        VersionSupport versionSupport = AnnotationUtils.findAnnotation(method, VersionSupport.class);
        return (versionSupport != null) ? new VersionRequestCondition(versionSupport.value()) : null;
    }


}
