/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.domain;

/**
 * @Author Lijl
 * @ClassName ThreadPoolProperties
 * @Description 线程参数
 * @Date 2021/10/15 10:37
 * @Version 1.0
 */

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool")
public class ThreadPoolProperties {
    private final int CORE_POOL_SIZE = 3;
    private final int MAX_POOL_SIZE = 10;
    private final int QUEUE_CAPACITY = 512;
    private final int KEEP_ALICE_SECONDS = 10;
    private final String THREAD_NAME_PREFIX = "query-hbase-thread-pool-%d";

    private int corePoolSize = CORE_POOL_SIZE;
    private int maxPoolSize = MAX_POOL_SIZE;
    private int queueCapacity = QUEUE_CAPACITY;
    private int keepAliveSeconds = KEEP_ALICE_SECONDS;
    private String threadNamePrefix = THREAD_NAME_PREFIX;

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

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }
}
