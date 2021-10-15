/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.service;

import com.huawei.commons.domain.KerberosProperties;
import com.huawei.commons.util.LoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private Configuration configuration;

    @Autowired
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
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
            String kerberosUser = kerberosProperties.getKerberosUser();
            String keytabPath = kerberosProperties.getKeytabPath();
            LoginUtil.setJaasConf(kerberosProperties.getZookeeperDefaultLoginContextName(), kerberosUser, keytabPath);
            LoginUtil.setZookeeperServerPrincipal(kerberosProperties.getZookeeperServerPrincipalKey(), kerberosProperties.getZookeeperDefaultServerPrincipal());
            LoginUtil.login(kerberosUser, keytabPath, kerberosProperties.getKerberosConfPath(), configuration);
        } catch (Exception e) {
            log.error("HBase login error:", e);
        }
        log.info("HBase login success.");
    }


    /**
     * * 定时更新凭证
     */
    private void startCheckKeytabTgtAndReloginJob() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                UserGroupInformation.getLoginUser().checkTGTAndReloginFromKeytab();
                log.warn("Check Kerberos Tgt And Relogin From Keytab Finish.");
            } catch (IOException e) {
                log.error("Check Kerberos Tgt And Relogin From Keytab Error", e);
            }
        }, 0, 10, TimeUnit.MINUTES); // 1s 后开始执行，每 3s 执行一次
        //10分钟循环 达到距离到期时间一定范围就会更新凭证
        log.warn("Start Check Keytab TGT And Relogin Job Success.");
    }
}
