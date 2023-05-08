package com.chen.codegeneration.demo.druid;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 实现多数数据源控制
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 数据源集合，需注意，key 类型必须和 {@link DataSourceContextHolder} 内 ThreadLocal 泛型类型一致
     */
    private static Map<Object, Object> dataSourceMap = new HashMap<>();

    private static final ReentrantLock lock = new ReentrantLock();

    private static volatile DynamicDataSource instance;

    /**
     * 获取实例
     */
    public static DynamicDataSource getInstance() {
        // 双重检查锁
        if (instance == null) {
            synchronized (DynamicDataSource.class) {
                if (instance == null) {
                    instance = new DynamicDataSource();
                }
            }
        }
        return instance;
    }

    public boolean containKey(DSType key) {
        return dataSourceMap.containsKey(key);
    }

    @SneakyThrows
    public void addDataSource(DSType key, DataSource dataSource) {
        Object o = dataSourceMap.get(key);
        if (o != null) {
            throw new Exception("数据库连接池 KEY 重复");
        }
        dataSourceMap.put(key, dataSource);
        setDataSourceMap(dataSourceMap);
    }


    public void setDataSourceMap(final Map<Object, Object> objectObjectMap) {
        lock.lock();
        dataSourceMap = objectObjectMap;
        super.setTargetDataSources(dataSourceMap);
        super.afterPropertiesSet();
        lock.unlock();
    }

    /**
     * 根据指定的 DSType 删除一个数据源
     *
     * @param key 数据源 DSType
     */
    @SneakyThrows
    public void delDataSource(DSType key) {
        Object o = dataSourceMap.get(key);
        if (o != null) {
            if (o instanceof DataSource) {
                DruidDataSource dataSource = (DruidDataSource) o;
                dataSource.close();
                dataSourceMap.remove(key);
                setDataSourceMap(dataSourceMap);
            } else {
                throw new Exception("数据池类型无法识别");
            }
        }
    }

    /**
     * 根据指定的 DSType 替换数据源
     *
     * @param key        数据源DSType
     * @param dataSource 数据源
     */
    public void replaceDataSource(DSType key, DataSource dataSource) {
        dataSourceMap.put(key, dataSource);
        setDataSourceMap(dataSourceMap);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSource();
    }
}
