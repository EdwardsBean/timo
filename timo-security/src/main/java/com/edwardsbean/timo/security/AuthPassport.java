package com.edwardsbean.timo.security;

/**
 * Created by edwardsbean on 2015/4/8 0008.
 */

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthPassport {
    boolean validate() default true;
}