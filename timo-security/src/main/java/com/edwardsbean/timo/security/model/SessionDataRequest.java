package com.edwardsbean.timo.security.model;

import com.edwardsbean.timo.security.code.SessionCommandCode;

/**
 * 获取SessionData的请求模型
 */
public class SessionDataRequest {
    /** 必须填充请求类型SSN_GET_SESSION_DATA_BY_SID
     * 非特殊情况，不要手动设置 */
    private int    cm = SessionCommandCode.SSN_GET_SESSION_DATA_BY_SID.getCode();
    /** 应用id */
    private int    apid;
    /** 终端用户IP ,对应接口请求参数的client_ip */
    private String cip;
    /** 是否保持链接,对应接口请求参数的keep_alive 1表示是，0表示否 */
    private int    keep_alive;
    /** 用户提供的SessionID  ,对应接口请求参数的session_id*/
    private String sid;
    /** 是否是半账号  ,对应接口请求参数的incomplete_user 1表示是，0表示否 */
    private int    incomplete_user;
    /** 是否快推账号（非真实性账号） ,对应接口请求参数的quick_user 1表示是，0表示否 */
    private int    quick_user;

    /** 是否需要cifinfo */
    private int    need_cinfo;

    public int getCm() {
        return cm;
    }

    public void setCm(int cm) {
        this.cm = cm;
    }

    public int getApid() {
        return apid;
    }

    public void setApid(int apid) {
        this.apid = apid;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getKeep_alive() {
        return keep_alive;
    }

    public void setKeep_alive(int keep_alive) {
        this.keep_alive = keep_alive;
    }

    public int getIncomplete_user() {
        return incomplete_user;
    }

    public void setIncomplete_user(int incomplete_user) {
        this.incomplete_user = incomplete_user;
    }

    public int getQuick_user() {
        return quick_user;
    }

    public void setQuick_user(int quick_user) {
        this.quick_user = quick_user;
    }

    public int getNeed_cinfo() {
        return need_cinfo;
    }

    public void setNeed_cinfo(int need_cinfo) {
        this.need_cinfo = need_cinfo;
    }

    @Override
    public String toString() {
        return "SessionDataRequest{" +
                "cm=" + cm +
                ", apid=" + apid +
                ", cip='" + cip + '\'' +
                ", keep_alive=" + keep_alive +
                ", sid='" + sid + '\'' +
                ", incomplete_user=" + incomplete_user +
                ", quick_user=" + quick_user +
                ", need_cinfo=" + need_cinfo +
                '}';
    }
}
