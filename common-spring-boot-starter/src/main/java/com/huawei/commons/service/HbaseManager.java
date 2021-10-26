/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.service;

import com.huawei.commons.domain.ZkProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Lijl
 * @ClassName HbaseManager
 * @Description Hbase实例管理
 * @Date 2021/10/14 16:32
 * @Version 1.0
 */
@Component
@EnableConfigurationProperties(ZkProperties.class)
public class HbaseManager {

    private ZkProperties zkProperties;

    @Autowired
    public void setZkProperties(ZkProperties zkProperties) {
        this.zkProperties = zkProperties;
    }


    private Kerberos kerberos;

    @Autowired
    public void setKerberos(Kerberos kerberos) {
        this.kerberos = kerberos;
    }

    /**
     * @Author lijiale
     * @MethodName getHbaseInstance
     * @Description 获取创建hbase实例
     * @Date 16:43 2021/10/14
     * @Version 1.0
     * @param
     * @return: com.huawei.querys.service.Hbase
    **/
    public HbaseOperations getHbaseInstance(){
        kerberos.login();
        HbaseOperations hbase = new Hbase(zkProperties);
        return hbase;
    }
}
