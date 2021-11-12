/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.manager;

import com.huawei.commons.domain.HbaseInstance;
import com.huawei.commons.domain.HbaseResourceProperties;
import com.huawei.commons.domain.ZkProperties;
import com.huawei.commons.impl.Hbase;
import com.huawei.commons.impl.HbaseOperations;
import com.huawei.commons.Kerberos;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @Author Lijl
 * @ClassName HbaseManager
 * @Description Hbase实例管理
 * @Date 2021/10/14 16:32
 * @Version 1.0
 */
@Slf4j
@Component
@EnableConfigurationProperties({ZkProperties.class, HbaseResourceProperties.class})
public class HbaseManager implements InitializingBean {

    private ZkProperties zkProperties;

    @Autowired
    public void setZkProperties(ZkProperties zkProperties) {
        this.zkProperties = zkProperties;
    }

    private HbaseResourceProperties hbaseResourceProperties;

    @Autowired
    public void setHbaseResourceProperties(HbaseResourceProperties hbaseResourceProperties) {
        this.hbaseResourceProperties = hbaseResourceProperties;
    }

    private Kerberos kerberos;

    @Autowired
    public void setKerberos(Kerberos kerberos) {
        this.kerberos = kerberos;
    }

    private CopyOnWriteArrayList<HbaseInstance> list = new CopyOnWriteArrayList<>();
    private final long timeOut = 1000 * 60 * 60 * 3;

    /**
     * @Author lijiale
     * @MethodName createHbaseInstance
     * @Description 创建hbase操作实例
     * @Date 17:58 2021/11/9
     * @Version 1.0
     * @param
     * @return: com.huawei.commons.domain.HbaseInstance
    **/
    private HbaseInstance createHbaseInstance(){
        HbaseInstance hbaseInstance = new HbaseInstance();
        hbaseInstance.setData(new Date());
        hbaseInstance.setHbaseOperations(getHbaseOperations());
        hbaseInstance.setStatus(0);
        hbaseInstance.setTimeout(false);
        return hbaseInstance;
    }


    /**
     * @Author lijiale
     * @MethodName getHbaseOperations
     * @Description 创建Hbase操作
     * @Date 17:58 2021/11/9
     * @Version 1.0
     * @param
     * @return: com.huawei.commons.service.HbaseOperations
    **/
    private HbaseOperations getHbaseOperations(){
        long startTime = System.currentTimeMillis();
        kerberos.login();
        HbaseOperations hbase = new Hbase(zkProperties);
        long endTime = System.currentTimeMillis();
        log.info("Kerberos认证，创建Hbase连接耗时:{}毫秒",endTime-startTime);
        return hbase;
    }

    /**
     * @Author lijiale
     * @MethodName checkHbaseInstance
     * @Description 检查Hbase操作资源是否过期，并清理过期操作资源创建新操作资源
     * @Date 17:59 2021/11/9
     * @Version 1.0
     * @param
     * @return: void
    **/
    private void checkHbaseInstance(){
        list.forEach(hbaseInstance -> {
            long endTime = System.currentTimeMillis();
            long startTime = hbaseInstance.getData().getTime();
            long l = endTime - startTime;
            boolean timeout = hbaseInstance.isTimeout();
            int status = hbaseInstance.getStatus();
            if (status==0){
                if (timeout){
                    hbaseInstance.getHbaseOperations().closeConnection();
                    list.remove(hbaseInstance);
                    list.add(createHbaseInstance());
                }else if (l>=timeOut){
                    hbaseInstance.getHbaseOperations().closeConnection();
                    list.remove(hbaseInstance);
                    list.add(createHbaseInstance());
                }
            }
        });
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
    public HbaseInstance getHbaseInstance(){
        checkHbaseInstance();
        HbaseInstance hbaseIt = null;
        log.info("开始获取Hbase操作资源实例");
        while (true){
            HbaseInstance hi = null;
            int count = 0;
            for (HbaseInstance hbaseInstance : list) {
                long endTime = System.currentTimeMillis();
                long startTime = hbaseInstance.getData().getTime();
                long l = endTime - startTime;
                if (l<timeOut){
                    int status = hbaseInstance.getStatus();
                    if (status==0){
                        hbaseInstance.setStatus(1);
                        hi = hbaseInstance;
                        break;
                    }
                    count++;
                    log.info("正在获取未使用的有效Hbase操作资源实例");
                }else{
                    count++;
                    hbaseInstance.setTimeout(true);
                }
            }
            if (hi!=null){
                hbaseIt = hi;
                break;
            }
            if (count>=hbaseResourceProperties.getCorePoolSize() && count< hbaseResourceProperties.getMaxPoolSize()){
                log.info("无空闲Hbase操作资源实例，开始创建新操作资源实例");
                list.add(createHbaseInstance());
            }
        }
        return hbaseIt;
    }

    /**
     * @Author lijiale
     * @MethodName afterPropertiesSet
     * @Description 初始化资源池
     * @Date 13:31 2021/11/10
     * @Version 1.0
     * @param
     * @return: void
    **/
    @Override
    public void afterPropertiesSet() {
        for (int i=0;i<hbaseResourceProperties.getCorePoolSize();i++){
            list.add(createHbaseInstance());
        }
        startCheckIdleAndTimeOut();
    }


    /**
     * @Author lijiale
     * @MethodName startCheckIdleAndTimeOut
     * @Description 启动守护线程检查空闲和过期的Hbase操作资源实例
     * @Date 13:23 2021/11/10
     * @Version 1.0
     * @param
     * @return: void
    **/
    private void startCheckIdleAndTimeOut() {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory
                        .Builder()
                        .namingPattern("scheduled-pool-%d")
                        .daemon(true)
                        .build());
        scheduledExecutorService.scheduleAtFixedRate(()->{
            checkHbaseInstance();
            int corePoolSize = hbaseResourceProperties.getCorePoolSize();
            int size = list.size();
            if (size>corePoolSize){
                int idleCount = size-corePoolSize;
                for (int i = 0; i < idleCount; i++) {
                    HbaseInstance hbaseInstance = list.get(i);
                    int status = hbaseInstance.getStatus();
                    if (status==0){
                        hbaseInstance.getHbaseOperations().closeConnection();
                        list.remove(hbaseInstance);
                    }
                }
            }
        }, 0, hbaseResourceProperties.getIdleTime(), TimeUnit.MINUTES);
    }
}
