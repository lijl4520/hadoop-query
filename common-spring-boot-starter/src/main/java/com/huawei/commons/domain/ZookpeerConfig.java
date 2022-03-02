/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author Lijl
 * @ClassName ZookpeerConfig
 * @Description 集群配置
 * @Date 2021/11/16 10:18
 * @Version 1.0
 */
@ConfigurationProperties(prefix = "hadoop.commons.zookeeper")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ZookpeerConfig {
    /**
     * Cross cluster configuration information
     */
    List<ZkProperties> config = new LinkedList<>();
    private final int CONFIG_SIZE = 1;
    private final boolean SINGLE_CLUSTER = true;
    /**
     * Number of cross clusters
     */
    private int configSize = CONFIG_SIZE;
    /**
     * Is it a single cluster
     */
    private boolean singleCluster = SINGLE_CLUSTER;

    public List<ZkProperties> getConfig() {
        return config;
    }

    public void setConfig(List<ZkProperties> config) {
        this.config = config;
    }

    public int getConfigSize() {
        return configSize;
    }

    public void setConfigSize(int configSize) {
        this.configSize = configSize;
    }

    public boolean isSingleCluster() {
        return singleCluster;
    }

    public void setSingleCluster(boolean singleCluster) {
        this.singleCluster = singleCluster;
    }
}
