package com.edwardsbean.timo.service.exception;

import org.springframework.web.bind.ServletRequestBindingException;


/**
 * 跨版本调用异常
 * @author edwardsbean
 * @date 15-5-7
 */
public class UnsupportedVersionException extends ServletRequestBindingException {

    private final String actualVersion;

    public UnsupportedVersionException(String actualVersion) {
        super("");
        this.actualVersion = actualVersion;
    }

    @Override
    public String getMessage() {
        return "Versions not support for " + this.actualVersion  + ",请不要请求比客户端版本高的服务端接口";
    }

}
