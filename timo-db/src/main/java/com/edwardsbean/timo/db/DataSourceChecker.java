package com.edwardsbean.timo.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.util.List;

/**
 * 检查数据源是否已成功创建，如果不成功，则直接退出系统
 *
 */
public class DataSourceChecker implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceChecker.class);
    private ApplicationContext applicationContext;
    private List<String> dataSourceNames;
    private String dataSourceName = "dataSource";

    /**
     * 根据配置检查数据源
     */
    public void check() {
        if (dataSourceNames == null || dataSourceNames.isEmpty()) {
            check(dataSourceName);
        } else {
            for (String name : dataSourceNames) {
                check(name);
            }
        }
    }

    private void check(String dataSourceName) {
        logger.info("Checking datasource {}", dataSourceName);
        Object bean = null;
        try {
            bean = applicationContext.getBean(dataSourceName);
        } catch(BeansException e) {
            exit(dataSourceName);
        }

        if (bean == null || !(bean instanceof DataSource)) {
            exit(dataSourceName);
        }

        logger.info("Datasource {} is OK!", dataSourceName);
    }

    private void exit(String dataSource) {
        logger.error("Error loading datasource {}", dataSource);
        System.exit(-1);
    }

    public void setDataSourceNames(List<String> dataSourceNames) {
        this.dataSourceNames = dataSourceNames;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}