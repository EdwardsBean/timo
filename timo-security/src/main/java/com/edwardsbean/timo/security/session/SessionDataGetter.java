package com.edwardsbean.timo.security.session;

import com.edwardsbean.timo.security.model.SessionDataRequest;
import com.edwardsbean.timo.security.model.SessionDataResponse;

/**
 * 与认证服务交互
 * Created by shicongyu01_91 on 2015/4/23.
 */
public interface SessionDataGetter {
    SessionDataResponse getSessionData(SessionDataRequest request);
}
