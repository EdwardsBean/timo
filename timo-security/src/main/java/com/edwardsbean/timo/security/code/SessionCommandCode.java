package com.edwardsbean.timo.security.code;

/**
 * Created by shicongyu01_91 on 2015/4/23.
 */
public enum SessionCommandCode {
    /**
     * 为用户产生合法的SessionID。当然，在应用程序发送SSN_SET_LOGINED_SESSION命令，
     * 而且没有提供合法的SessionID，Session服务器也会实现为用户产生SessionID的功能
     */
    SSN_GET_SESSION_ID(0x00101, "获取SessionID"),
    /**
     * 如果应用程序发现浏览器提供BaiduUSS，其中包含有效的SessionID，则可以向Session服务器发送这个命令，
     * 以检验用户身份，并且获得Session数据
     */
    SSN_GET_SESSION_DATA_BY_SID(0x00102, "获取Session数据（提供SessionID），包括共有Session数据和私有Session数据"),
    /**
     * 用户在应用程序（一般指Passport的中心User程序）中登录成功，
     * 则需要给Session发送数据以告知所有应用，该用户已经登录成功。此时应该使用该命令
     */
    SSN_SET_LOGINED_SESSION(0x00103, "登记用户登录成功Session（提供或者不提供SessionID，提供UID、UN和公共Session保留数据）"),
    /**
     * 登记用户退出成功Session
     */
    SSN_SET_OFFLINE_SESSION(0x00104, "登记用户退出成功Session"),
    /**
     * 部分应用程序需要维护全局性数据（全局性数据的定义在后续额外说明）
     * ，则它们需要根据实际需要修改供所有应用使用的全局性数据,应用程序需要修改自己的私有Session数据时，也可以发起该命令，此时rmod_mask需要设置为0
     */
    SSN_MOD_SESSION_DATA(0x00201, "修改Session数据");
    /**
     * 编码
     */
    private int code;
    /**
     * 描述
     */
    private String description;

    private SessionCommandCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
