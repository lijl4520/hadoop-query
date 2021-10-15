/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.config;

import com.huawei.commons.domain.KerberosProperties;
import com.huawei.commons.domain.ZkProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @Author Lijl
 * @ClassName KerberosConfiguration
 * @Description Kerberos鉴权配置类
 * @Date 2021/10/15 14:24
 * @Version 1.0
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties({KerberosProperties.class,ZkProperties.class})
public class KerberosConfiguration {

    private ZkProperties zkProperties;

    @Autowired
    public void setZkProperties(ZkProperties zkProperties) {
        this.zkProperties = zkProperties;
    }

    private KerberosProperties kerberosProperties;

    @Autowired
    public void setKerberosProperties(KerberosProperties kerberosProperties) {
        this.kerberosProperties = kerberosProperties;
    }

    /**
     * @Author lijiale
     * @MethodName configuration
     * @Description 封装Kerberos鉴权配置
     * @Date 14:24 2021/10/15
     * @Version 1.0
     * @param
     * @return: org.apache.hadoop.conf.Configuration
    **/
    @Bean
    public Configuration configuration() {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("hbase.security.authentication", "kerberos");
        conf.set("hbase.master.kerberos.principal", kerberosProperties.getPrincipal());
        conf.set("hbase.regionserver.kerberos.principal", kerberosProperties.getPrincipal());
        conf.set("hbase.zookeeper.quorum", zkProperties.getZookeeperQuorum());
        conf.set("hbase.zookeeper.property.clientPort", zkProperties.getZookeeperClientPort());
        conf.set("zookeeper.znode.parent", kerberosProperties.getZnodeParent());
        return conf;
    }
}
