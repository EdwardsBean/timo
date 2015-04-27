package com.edwardsbean.timo.db.sharding.strategy;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 根据时间分表
 * @author edwardsbean
 * @date 2015/4/27.
 */
public class TimingStrategy extends BaseShardStragety implements ShardStrategy {

    private static final Log logger = LogFactory.getLog(TimingStrategy.class);
    //java.util.Calendar的静态变量，表示获取时间的字段名，比如day_of_month, day_of_week等
    private String timingRule;

    public String getTargetTableName(String baseTableName, Object params,
                                     String mapperId) {

        Date value = (Date)getKeyFieldValue(params);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        Field timingField = ReflectionUtils.findField(Calendar.class, timingRule.toUpperCase());
        int timingValue = 0;
        try {

            timingValue = calendar.get(timingField.getInt(null));
        } catch (Exception e) {
            logger.error("GET_TIMING_VALUE_ERROR, date = " + value + ", timingRule = " + timingRule, e);
            throw new RuntimeException("get timing value error, date = " + value + ", timingRule = " + timingRule, e);
        }

        DecimalFormat format = new DecimalFormat(targetTableNamePattern);
        return format.format(timingValue % divisor);
    }

    public String getTimingRule() {
        return timingRule;
    }

    public void setTimingRule(String timingRule) {
        this.timingRule = timingRule;
    }

}
