package com.chen.codegeneration.demo.druid;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DataSourceContextHolder {

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     */
    private static final ThreadLocal<DSType> CONTEXT_HOLDER = new ThreadLocal<>();

    public static DSType getDataSource() {
        return CONTEXT_HOLDER.get();
    }

    public static void setDataSource(DSType dataSourceType) {
        CONTEXT_HOLDER.set(dataSourceType);
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

}
