package com.chen.codegeneration.demo.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static com.chen.codegeneration.demo.druid.DSType.MASTER;
import static com.chen.codegeneration.demo.druid.DSType.SLAVE;

/**
 * 动态数据源配置
 */
@Configuration
public class DynamicDataSourceConfig {

    @Bean("masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.master")
    public DruidDataSource dataSourceMaster() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("slaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.slave")
    public DruidDataSource dataSourceSlave() {
        return DruidDataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "dynamicDataSource")
    public DynamicDataSource dynamicDataSource(@Qualifier("masterDataSource") DataSource master,
                                               @Qualifier("slaveDataSource") DataSource slave) {
        DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();

        // 配置多数据源
        dynamicDataSource.addDataSource(MASTER, master);
        dynamicDataSource.addDataSource(SLAVE, slave);
        // 设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(master);

        return dynamicDataSource;
    }


    /**
     * 配置@Transactional注解事务
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }

}
