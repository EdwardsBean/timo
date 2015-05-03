package com.edwardsbean.timo.db.sharding.strategy;

import java.text.DecimalFormat;

/**
 * 根据指定字段取模
 * @author edwardsbean
 * @date 2015/4/27.
 */
public class KeyModStrategy extends BaseShardStragety implements ShardStrategy {

    public String getTargetTableName(String baseTableName, Object params,
                                     String mapperId) {

        Long value = (Long)getKeyFieldValue(params);

        DecimalFormat format = new DecimalFormat(targetTableNamePattern);
        return format.format(value % divisor);
    }

}
