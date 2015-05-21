package com.edwardsbean.timo.service.model;

/**
 * Created by edwardsbean on 14-10-29.
 */
public class Msg {
    private String code = MsgCode.SUCCESS.getCode();
    private String msg = "";
    protected Object returnData;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getReturnData() {
        return returnData;
    }

    public void setReturnData(Object returnData) {
        this.returnData = returnData;
    }


    public Msg(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Msg(Object returnData) {
        this.returnData = returnData;
    }

    public Msg() {
    }

    @Override
    public String toString() {
        return "Msg{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", returnData=" + returnData +
                '}';
    }
}
