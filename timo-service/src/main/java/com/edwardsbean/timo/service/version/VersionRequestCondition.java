package com.edwardsbean.timo.service.version;

import com.edwardsbean.timo.common.Conventions;
import com.edwardsbean.timo.common.VersionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;

/**
 * 自动路由版本号匹配
 * @author edwardsbean
 * @date 2015/5/2 0002.
 */
public class VersionRequestCondition implements RequestCondition<VersionRequestCondition> {
    private final VersionExpression versionExpression;

    public VersionRequestCondition(String version) {
        this.versionExpression = new VersionExpression(version);
    }

    /**
     * Type level和Method level的注解，要用谁的值或者合并。对于只有Method level的，则无视
     * @param other
     * @return
     */
    @Override
    public VersionRequestCondition combine(VersionRequestCondition other) {
        return this;
    }

    /**
     * 根据客户端版本号，匹配服务端接口。
     * 客户端不能请求高版本的接口
     * @param request
     * @return
     */
    @Override
    public VersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (!versionExpression.match(request)) {
            return null;
        }
        return this;
    }

    /**
     * 优先最近一个版本的接口
     * @param other
     * @param request
     * @return
     */
    @Override
    public int compareTo(VersionRequestCondition other, HttpServletRequest request) {
        return VersionUtil.compare(other.versionExpression.version, this.versionExpression.version);
    }

    public VersionExpression getVersionExpression() {
        return versionExpression;
    }

    static class VersionExpression {
        private final String version;

        public VersionExpression(String version) {
            this.version = delPrefix(version);
        }

        public String delPrefix(String version) {
            String result = StringUtils.lowerCase(version);
            if (result != null && result.startsWith(Conventions.VERSION_PREFIX)) {
                result = result.substring(1, version.length());
            }
            return result;
        }
        public boolean match(HttpServletRequest request) {
            String clientVersion = request.getHeader(Conventions.DIVIDE_VERSION);
            clientVersion = delPrefix(clientVersion);
            return clientVersion != null && VersionUtil.compare(clientVersion,version) >= 0;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return "VersionExpression{" +
                    "version='" + version + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "VersionRequestCondition{" +
                "versionExpression=" + versionExpression +
                '}';
    }
}
