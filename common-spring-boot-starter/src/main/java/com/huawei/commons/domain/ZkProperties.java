/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author Lijl
 * @ClassName ZkProperties
 * @Description TODO
 * @Date 2021/10/14 16:02
 * @Version 1.0
 */
@ConfigurationProperties(prefix = "hadoop.commons.zookeeper.config")
public class ZkProperties {
    private final String ZOOKEEPER_CLIENT_PORT = "2181";
    private String zookeeperQuorum;
    private String zookeeperClientPort = ZOOKEEPER_CLIENT_PORT;

    public String getZookeeperQuorum() {
        return zookeeperQuorum;
    }

    public void setZookeeperQuorum(String zookeeperQuorum) {
        this.zookeeperQuorum = zookeeperQuorum;
    }

    public String getZookeeperClientPort() {
        return zookeeperClientPort;
    }

    public void setZookeeperClientPort(String zookeeperClientPort) {
        this.zookeeperClientPort = zookeeperClientPort;
    }
}
