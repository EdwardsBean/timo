package com.edwardsbean.timo.service.health;

import com.edwardsbean.timo.common.AppConfigUtil;
import com.edwardsbean.timo.common.Conventions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 健康检查REST服务
 *
 */
@Controller
@RequestMapping(Conventions.SERVICES_URL_PREFIX)
public class HealthStatusController {
    /**
     * 获取服务器健康状态
     *
     * 请求地址：/services/_health/status
     * METHOD ：GET
     * 返回格式：JSON
     */
    @ResponseBody
    @RequestMapping(value = "/_health/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getHealthStatus() {
        Map<String, String> status = new HashMap<String, String>();
        String appName = AppConfigUtil.getProperty("app.name");
        status.put("status", "OK");
        status.put("appName", appName);
        status.put("gmtCheck", (new Date()).toString());
        status.put("webPort", AppConfigUtil.getProperty(StringUtils.join(appName, ".nginx.listen.port")));
        status.put("servicesPort", AppConfigUtil.getProperty(StringUtils.join(appName, ".nginx.service.listen.port")));
        status.put("tomcatPort", AppConfigUtil.getProperty(StringUtils.join(appName, ".tomcat.connector.port")));
        return status;
    }
}
