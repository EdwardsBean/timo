package com.edwardsbean.timo.security.code;

/**
 * 调用passport的session接口的返回码
 *
 */
public enum SessionQueryResultCode {
    SSN_RSTATUS_OK(0x000, "成功"),
    SSN_RSTATUS_DENY(0x001, "不符合IP与应用绑定要求，访问被拒绝"),
    SSN_RSTATUS_DENY_ATTACK(0x002, "拒绝为可能的攻击服务"),
    SSN_RSTATUS_SERVER_BUSY(0x004, "服务器暂时不能提供服务"),
    SSN_RSTATUS_INVALID_SID(0x008, "提供的SessionID不正确"),
    SSN_RSTATUS_NO_POWER(0x010, "没有权限修改相应数据"),
    SSN_RSTATUS_INVALID_PARAM(0x020, "输入不满足要求"),
    SSN_RSTATUS_OFFLINE(0x040, "用户未上线"),
    SSN_RSTATUS_UNKNOWN(0x800, "未知的错误"),
    ;

    /**
     * 编码
     */
    private Integer code;
    /**
     * 描述
     */
    private String  description;

    private SessionQueryResultCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SessionQueryResultCode getByCode(int code) {
        for (SessionQueryResultCode resultCode : SessionQueryResultCode.values()) {
            if (resultCode.getCode() == code) {
                return resultCode;
            }
        }
        return null;
    }
}
