/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.domain;
/**
 * @Author Lijl
 * @ClassName ZkProperties
 * @Description zk集群参数
 * @Date 2021/10/14 16:02
 * @Version 1.0
 */
public class ZkProperties {
    private final String ZOOKEEPER_CLIENT_PORT = "2181";
    private final String ZOOKEEPER_QUORUM = "hebsjzx-schbase-master-47-158,hebsjzx-schbase-master-47-75,hebsjzx-schbase-master-47-96";
    private final String QUORUM_NAME = "BJ";
    /**
     * Zookeeper Cluster address
     */
    private String zookeeperQuorum = ZOOKEEPER_QUORUM;
    /**
     * Zookeeper Cluster port
     */
    private String zookeeperClientPort = ZOOKEEPER_CLIENT_PORT;
    /**
     * Cluster node
     */
    private String znodeParent;
    /**
     * Hbase principal
     */
    private String principal;
    /**
     * Cluster name
     */
    private String quorumName = QUORUM_NAME;

    private String sitePath;

    private String corePath;

    private String hdfsPath;

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

    public String getZnodeParent() {
        return znodeParent;
    }

    public void setZnodeParent(String znodeParent) {
        this.znodeParent = znodeParent;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getQuorumName() {
        return quorumName;
    }

    public void setQuorumName(String quorumName) {
        this.quorumName = quorumName;
    }

    public String getSitePath() {
        return sitePath;
    }

    public void setSitePath(String sitePath) {
        this.sitePath = sitePath;
    }

    public String getCorePath() {
        return corePath;
    }

    public void setCorePath(String corePath) {
        this.corePath = corePath;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }
}
