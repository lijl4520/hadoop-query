/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author Lijl
 * @ClassName HbaseResourceProperties
 * @Description Hbase操作资源池配置
 * @Date 2021/11/10 10:32
 * @Version 1.0
 */
@ConfigurationProperties(prefix = "hadoop.commons.hbase-resource-pool")
public class HbaseResourceProperties {
    private final int CORE_POOL_SIZE = 10;
    private final int MAX_POOL_SIZE = 50;
    private final int IDLE_TIME = 30;
    /**
     * Number of initial connections
     */
    private int corePoolSize = CORE_POOL_SIZE;
    /**
     * Maximum connection
     */
    private int maxPoolSize = MAX_POOL_SIZE;
    /**
     * Connection idle time
     */
    private int idleTime = IDLE_TIME;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }
}
