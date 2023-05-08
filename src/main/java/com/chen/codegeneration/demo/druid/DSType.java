package com.chen.codegeneration.demo.druid;

import javax.sql.DataSource;

/**
 * 数据源枚举
 * <p>
 * 对应的数据源配置在 application.yml 中 spring.datasource.druid 下配置，
 * 并且在 {@link DynamicDataSourceConfig} 中添加对应的数据源，
 * 然后再将 Datasource Bean 添加在 {@link DynamicDataSourceConfig#dynamicDataSource(DataSource, DataSource)}} 内,
 * 最后在此处添加对应的枚举即可。
 *
 * @author CHE&N
 * @see DSAspect
 */
public enum DSType {

    /**
     * 主库
     */
    MASTER,

    /**
     * 从库
     */
    SLAVE

}
