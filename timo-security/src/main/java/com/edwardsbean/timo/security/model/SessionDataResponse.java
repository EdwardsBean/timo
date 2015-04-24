package com.edwardsbean.timo.security.model;

import com.edwardsbean.timo.security.code.SessionQueryResultCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * SessionData 获取结果
 */
public class SessionDataResponse {
    /** 返回状态（对应 SessionQueryResultCode 中的状态），
     * 可能的错误码：
     *  SSN_RSTATUS_DENY：拒绝访问
     SSN_RSTATUS_DENY_ATTACK：拒绝为可能的攻击服务
     SSN_RSTATUS_SERVER_BUSY：暂时无法提供服务
     SSN_RSTATUS_INVALID_SID：提供的SID不正确，重新生成了新的SessionID
     * 如果SSN_RSTATUS_OK，则uid/username/global_data/private_data有效。
     */
    private int     status = SessionQueryResultCode.SSN_RSTATUS_UNKNOWN.getCode();
    /** session_id char[128]位*/
    private String  session_id;
    /** 用户ID，如果用户没有登录，则ID为0 */
    private String  uid;
    /** 登录用户名，char[32]位，如果没有登录，则为空（第一个Byte为0） */
    private String  username;
    /** 全局Session数据 ,char[32]位*/
    private char[]  global_data;
    /** 私有Session数据,char[128]位 */
    private char[]  private_data;
    /** 指示是否需要重置用户Cookie*/
    private boolean need_reset_cookie;
    /** session创建时间 */
    private long    created_time;

    /** 最后登录时间 */
    private long    last_login_time;

    /** 本次会话访问总次数  */
    private int     access_count;

    /** 最后更新时间  */
    private int     last_updated_time;

    /** 访问所有应用的最后时间  */
    private int     global_access_time;
    /** 访问本应用的最后时间  */
    private int     private_access_time;
    /** 是否记住密码：1为记住密码，0为未记住密码 */
    private int     pwd_flag;
    /** 保留数据 */
    private int     reserved;
    private String  secureemail;
    private String  securemobil;
    private String  cinfo;

    public SessionQueryResultCode getStatusEnum() {
        return SessionQueryResultCode.getByCode(status);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getGlobal_data() {
        return global_data;
    }

    public void setGlobal_data(char[] global_data) {
        this.global_data = global_data;
    }

    public char[] getPrivate_data() {
        return private_data;
    }

    public void setPrivate_data(char[] private_data) {
        this.private_data = private_data;
    }

    public boolean isNeed_reset_cookie() {
        return need_reset_cookie;
    }

    public void setNeed_reset_cookie(boolean need_reset_cookie) {
        this.need_reset_cookie = need_reset_cookie;
    }

    public long getCreated_time() {
        return created_time;
    }

    public void setCreated_time(long created_time) {
        this.created_time = created_time;
    }

    public long getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(long last_login_time) {
        this.last_login_time = last_login_time;
    }

    public int getAccess_count() {
        return access_count;
    }

    public void setAccess_count(int access_count) {
        this.access_count = access_count;
    }

    public int getLast_updated_time() {
        return last_updated_time;
    }

    public void setLast_updated_time(int last_updated_time) {
        this.last_updated_time = last_updated_time;
    }

    public int getGlobal_access_time() {
        return global_access_time;
    }

    public void setGlobal_access_time(int global_access_time) {
        this.global_access_time = global_access_time;
    }

    public int getPrivate_access_time() {
        return private_access_time;
    }

    public void setPrivate_access_time(int private_access_time) {
        this.private_access_time = private_access_time;
    }

    public int getPwd_flag() {
        return pwd_flag;
    }

    public void setPwd_flag(int pwd_flag) {
        this.pwd_flag = pwd_flag;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public String getSecureemail() {
        return secureemail;
    }

    public void setSecureemail(String secureemail) {
        this.secureemail = secureemail;
    }

    public String getSecuremobil() {
        return securemobil;
    }

    public void setSecuremobil(String securemobil) {
        this.securemobil = securemobil;
    }

    public boolean isValid() {
        return SessionQueryResultCode.SSN_RSTATUS_OK.equals(SessionQueryResultCode
                .getByCode(status)) && !StringUtils.isBlank(session_id);
    }

    public String getCinfo() {
        return cinfo;
    }

    public void setCinfo(String cinfo) {
        this.cinfo = cinfo;
    }

    @Override
    public String toString() {
        return "SessionDataResponse{" +
                "status=" + status +
                ", session_id='" + session_id + '\'' +
                ", uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", global_data=" + Arrays.toString(global_data) +
                ", private_data=" + Arrays.toString(private_data) +
                ", need_reset_cookie=" + need_reset_cookie +
                ", created_time=" + created_time +
                ", last_login_time=" + last_login_time +
                ", access_count=" + access_count +
                ", last_updated_time=" + last_updated_time +
                ", global_access_time=" + global_access_time +
                ", private_access_time=" + private_access_time +
                ", pwd_flag=" + pwd_flag +
                ", reserved=" + reserved +
                ", secureemail='" + secureemail + '\'' +
                ", securemobil='" + securemobil + '\'' +
                ", cinfo='" + cinfo + '\'' +
                '}';
    }
}
