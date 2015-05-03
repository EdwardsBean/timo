package com.edwardsbean.timo.db.sharding.strategy;

/**
 * 不进行分表的策略，供测试用
 * @author edwardsbean
 * @date 2015/4/27.
 */
public class NoShardStrategy implements ShardStrategy {

    /* (non-Javadoc)
     * @see com.google.code.shardbatis.strategy.ShardStrategy#getTargetTableName(java.lang.String, java.lang.Object, java.lang.String)
     */
    public String getTargetTableName(String baseTableName, Object params,
                                     String mapperId) {
        return baseTableName;
    }

}