package com.edwardsbean.timo.service.version;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( ElementType.METHOD )
@Retention(RetentionPolicy.RUNTIME)
public @interface VersionSupport {
    String value() default "v1.0";
}