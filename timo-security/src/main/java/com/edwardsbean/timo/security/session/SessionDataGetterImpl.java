package com.edwardsbean.timo.security.session;

import com.edwardsbean.timo.common.AppConfigUtil;
import com.edwardsbean.timo.security.code.SessionQueryResultCode;
import com.edwardsbean.timo.security.model.SessionDataRequest;
import com.edwardsbean.timo.security.model.SessionDataResponse;
import com.edwardsbean.timo.service.client.RestClientSupport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ObjectToStringHttpMessageConverter;

/**
 * 调用passport平台的校验session接口，校验session，并获取session数据
 * @author chenxingbo
 * @version $Id: SessionDataGetterImpl1.java, v 0.1 2014年4月23日 下午6:05:26 chenxingbo Exp $
 */
public class SessionDataGetterImpl extends RestClientSupport implements SessionDataGetter {
    private static final Logger logger             = LoggerFactory
            .getLogger(SessionDataGetterImpl.class);
    private String              passportServiceURL = "/ssn";

    public SessionDataGetterImpl() {
        //做初始化工作，可以是从配置文件中读取session服务提供者
    }

    /**
     * 去passport平台查询session
     */
    @Override
    public SessionDataResponse getSessionData(SessionDataRequest request) {
        SessionDataResponse dataResponse = getForObject(passportServiceURL,
                SessionDataResponse.class, request);
        dataResponse = dataResponse == null ? new SessionDataResponse() : dataResponse;
        //记日志
        logResult(dataResponse);
        return dataResponse;
    }

    /**
     * 记日志
     */
    private void logResult(SessionDataResponse dataResponse) {
        logger.info("passport返回\"状态{}\",结果:{}",
                SessionQueryResultCode.getByCode(dataResponse.getStatus()).getDescription(),
                dataResponse.toString());
    }

}
