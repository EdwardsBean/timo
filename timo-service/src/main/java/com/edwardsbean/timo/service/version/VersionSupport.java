package com.edwardsbean.timo.service.version;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 版本路由标签，服务的根据Http Header中的DivideVersion自动路由，选择控制器接口，策略如下：
 * 1：Http Header没有DivideVersion版本信息，则自动选取最新版本接口
 * 2：Http Header有DivideVersion版本信息，则自动选择最近的版本接口
 * 3：不允许跨高版本调用
 *
 * 注意，务必使不同版本的接口的@RequestMapping配置成一样，否则由于@RequestMapping的配置优先级高于VersionCondition，使得
 * 路由规则无法生效。
 * @author edwardsbean
 * @date 2015/5/2 0002.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VersionSupport {
    String value() default "V0";
}