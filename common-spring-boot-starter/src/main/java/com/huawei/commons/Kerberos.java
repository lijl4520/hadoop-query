/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons;

import com.huawei.commons.domain.KerberosProperties;
import com.huawei.commons.util.LoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Lijl
 * @ClassName Kerberos
 * @Description Kerberos鉴权
 * @Date 2021/10/14 16:29
 * @Version 1.0
 */
@EnableConfigurationProperties(KerberosProperties.class)
@Component
@Slf4j
public class Kerberos {

    KerberosProperties kerberosProperties;

    @Autowired
    public void setKerberosProperties(KerberosProperties kerberosProperties) {
        this.kerberosProperties = kerberosProperties;
    }

    /**
     * @Author lijiale
     * @MethodName login
     * @Description Kerberos鉴权
     * @Date 16:30 2021/10/14
     * @Version 1.0
     * @param
     * @return: void
    **/
    public void login() {
        try {
            Configuration conf = HBaseConfiguration.create();
            conf.set("hadoop.security.authentication", "kerberos");
            String kerberosUser = kerberosProperties.getKerberosUser();
            String keytabPath = kerberosProperties.getKeytabPath();
            log.info("keytabPath===>{}",keytabPath);
            LoginUtil.setJaasConf(kerberosProperties.getZookeeperDefaultLoginContextName(),kerberosUser, keytabPath);
            log.info("ServerPrincipal===>{}",kerberosProperties.getZookeeperDefaultServerPrincipal());
            LoginUtil.login(kerberosUser, keytabPath, kerberosProperties.getKerberosConfPath(), conf);
        } catch (Exception e) {
            log.error("HBase login error:", e);
        }
        log.info("HBase login success.");
    }
}
