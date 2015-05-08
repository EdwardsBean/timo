package com.edwardsbean.timo.service.model;

/**
 * @author edwardsbean
 * @date 15-5-8
 */
public enum MsgCode {
    SUCCESS("0", "成功"),
    NOIMP("-1", "接口未实现"),
    SYSTEM_ERROR("999", "系统发生内部错误");


    private String code;
    private String msg;

    MsgCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
