/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author Lijl
 * @ClassName HadoopProperties
 * @Description kerberos 认证参数
 * @Date 2021/10/14 11:22
 * @Version 1.0
 */
@ConfigurationProperties(prefix = "hadoop.commons.kerberos.config")
public class KerberosProperties {

    private final String ZOOKEEPER_DEFAULT_LOGIN_CONTEXTNAME = "Client";
    private final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";

    private String keytabPath;
    private String kerberosConfPath;
    private String kerberosUser;
    private String zookeeperDefaultLoginContextName = ZOOKEEPER_DEFAULT_LOGIN_CONTEXTNAME;
    private String zookeeperServerPrincipalKey = ZOOKEEPER_SERVER_PRINCIPAL_KEY;
    private String zookeeperDefaultServerPrincipal;
    private String principal;
    private String znodeParent;

    public String getKeytabPath() {
        return keytabPath;
    }

    public void setKeytabPath(String keytabPath) {
        this.keytabPath = keytabPath;
    }

    public String getKerberosConfPath() {
        return kerberosConfPath;
    }

    public void setKerberosConfPath(String kerberosConfPath) {
        this.kerberosConfPath = kerberosConfPath;
    }

    public String getKerberosUser() {
        return kerberosUser;
    }

    public void setKerberosUser(String kerberosUser) {
        this.kerberosUser = kerberosUser;
    }

    public String getZookeeperDefaultLoginContextName() {
        return zookeeperDefaultLoginContextName;
    }

    public void setZookeeperDefaultLoginContextName(String zookeeperDefaultLoginContextName) {
        this.zookeeperDefaultLoginContextName = zookeeperDefaultLoginContextName;
    }

    public String getZookeeperServerPrincipalKey() {
        return zookeeperServerPrincipalKey;
    }

    public void setZookeeperServerPrincipalKey(String zookeeperServerPrincipalKey) {
        this.zookeeperServerPrincipalKey = zookeeperServerPrincipalKey;
    }

    public String getZookeeperDefaultServerPrincipal() {
        return zookeeperDefaultServerPrincipal;
    }

    public void setZookeeperDefaultServerPrincipal(String zookeeperDefaultServerPrincipal) {
        this.zookeeperDefaultServerPrincipal = zookeeperDefaultServerPrincipal;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getZnodeParent() {
        return znodeParent;
    }

    public void setZnodeParent(String znodeParent) {
        this.znodeParent = znodeParent;
    }
}
