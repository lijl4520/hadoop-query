/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.manager;

import com.huawei.commons.domain.HbaseInstance;
import com.huawei.commons.domain.HbaseResourceProperties;
import com.huawei.commons.domain.ZkProperties;
import com.huawei.commons.domain.ZookpeerConfig;
import com.huawei.commons.impl.Hbase;
import com.huawei.commons.impl.HbaseOperations;
import com.huawei.commons.Kerberos;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;
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
@EnableConfigurationProperties({ZookpeerConfig.class, HbaseResourceProperties.class})
public class HbaseManager implements InitializingBean {

    private ZookpeerConfig zookpeerConfig;

    @Autowired
    public void setZookpeerConfig(ZookpeerConfig zookpeerConfig) {
        this.zookpeerConfig = zookpeerConfig;
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

    private Map<String,CopyOnWriteArrayList<HbaseInstance>> map = new ConcurrentHashMap<>(16);
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
    private HbaseInstance createHbaseInstance(ZkProperties zkProperties){
        HbaseInstance hbaseInstance = new HbaseInstance();
        hbaseInstance.setData(new Date());
        hbaseInstance.setHbaseOperations(getHbaseOperations(zkProperties));
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
    private HbaseOperations getHbaseOperations(ZkProperties zkProperties){
        long startTime = System.currentTimeMillis();
        kerberos.login();
        HbaseOperations hbase = new Hbase(zkProperties);
        long endTime = System.currentTimeMillis();
        log.info("创建Hbase连接耗时:{}毫秒",endTime-startTime);
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
        map.forEach((k,v)-> v.forEach(hbaseInstance -> {
                long endTime = System.currentTimeMillis();
                long startTime = hbaseInstance.getData().getTime();
                long l = endTime - startTime;
                boolean timeout = hbaseInstance.isTimeout();
                int status = hbaseInstance.getStatus();
                if (status==0){
                    if (timeout){
                        hbaseInstance.getHbaseOperations().closeConnection();
                        v.remove(hbaseInstance);
                        v.add(createHbaseInstance(k));
                    }else if (l>=timeOut){
                        hbaseInstance.getHbaseOperations().closeConnection();
                        v.remove(hbaseInstance);
                        v.add(createHbaseInstance(k));
                    }
                }
            })
        );
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
    public synchronized Map<String,HbaseInstance> getHbaseInstance(String dbSourcetype){
        checkHbaseInstance();
        log.info("开始获取Hbase操作资源实例");
        Map<String,HbaseInstance> hbaseInstances = new HashMap<>(16);
        /*
         * 1.判断单个集群连接还是取所有集群连接
         * 2.遍历集群连接池、获取集群连接
         */
        while (true){
            //判断是否获取单个集群连接否则获取所有集群连接 并跳出循环返回
            if (dbSourcetype!=null && !zookpeerConfig.isSingleCluster()){
                HbaseInstance hbaseInstance = hbaseInstances.get(dbSourcetype);
                if (hbaseInstance!=null){
                    break;
                }
            }else{
                int configSize = zookpeerConfig.getConfigSize();
                if (configSize==hbaseInstances.size()){
                    break;
                }
            }
            //遍历连接池
            for (Map.Entry<String, CopyOnWriteArrayList<HbaseInstance>> listEntry : map.entrySet()) {
                String key = listEntry.getKey();
                //判断连接池是否已存在;存在跳过循环，否则继续执行
                HbaseInstance val = hbaseInstances.get(key);
                if (val!=null){
                    continue;
                }
                CopyOnWriteArrayList<HbaseInstance> list = listEntry.getValue();
                HbaseInstance hi = null;
                int count = 0;
                boolean skipC = true;
                /*
                 * 1.遍历单个连接池
                 * 2.判断单个连接池中已到过期时间与未到过期时间的连接
                 *      2.1 判断未过期的连接是否正在被使用；使用中做统计，暂未使用设置连接使用中并取出待使用
                 *      2.2 已过期的连接做统计 并设置已过期（待后续过期检查移除）
                 * 3.判断统计是否已达到初始连接数且还未达到最大连接数，满足条件创建新连接 否则跳过继续执行
                 * 4.将取出的暂未使用的连接放入临时集合待返回使用
                 */
                for (HbaseInstance hbaseInstance : list) {
                    long endTime = System.currentTimeMillis();
                    long startTime = hbaseInstance.getData().getTime();
                    long l = endTime - startTime;
                    if (l<timeOut){
                        int status = hbaseInstance.getStatus();
                        if (status==0){
                            hbaseInstance.setStatus(1);
                            hi = hbaseInstance;
                            skipC = false;
                            break;
                        }
                        count++;
                        log.info("正在获取未使用的有效Hbase操作资源实例");
                    }else{
                        count++;
                        hbaseInstance.setTimeout(true);
                    }
                }
                if (skipC && count>=hbaseResourceProperties.getCorePoolSize()
                        && count< hbaseResourceProperties.getMaxPoolSize()){
                    log.info("无空闲Hbase操作资源实例，开始创建新操作资源实例");
                    list.add(createHbaseInstance(key));
                }
                if (hi!=null){
                    hbaseInstances.put(key,hi);
                }
            }
        }
        return hbaseInstances;
    }

    /**
     * @Author lijiale
     * @MethodName createHbaseInstance
     * @Description 创建单个集群连接
     * @Date 13:57 2021/11/16
     * @Version 1.0
     * @param key
     * @return: com.huawei.commons.domain.HbaseInstance
    **/
    private HbaseInstance createHbaseInstance(String key) {
        HbaseInstance hbaseInstance = null;
        for (ZkProperties zkProperties : zookpeerConfig.getConfig()) {
            String quorumName = zkProperties.getQuorumName();
            if (quorumName.equals(key)){
                hbaseInstance = createHbaseInstance(zkProperties);
                break;
            }
        }
        return hbaseInstance;
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
        /*List<ZkProperties> zkPropertiesList = zookpeerConfig.getConfig();
        int configSize = zookpeerConfig.getConfigSize();
        for (int i = 0; i < configSize; i++) {
            ZkProperties zkProperties = zkPropertiesList.get(i);
            CopyOnWriteArrayList<HbaseInstance> list = new CopyOnWriteArrayList<>();
            for (int j=0;j<hbaseResourceProperties.getCorePoolSize();j++){
                list.add(createHbaseInstance(zkProperties));
            }
            map.put(zkProperties.getQuorumName(),list);
        }
        startCheckIdleAndTimeOut();*/
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
            int corePoolSize = hbaseResourceProperties.getCorePoolSize();
            synchronized (this){
                checkHbaseInstance();
                map.forEach((k,v)->{
                    int size = v.size();
                    if (size>corePoolSize){
                        int idleCount = size-corePoolSize;
                        for (int i = 0; i < idleCount; i++) {
                            HbaseInstance hbaseInstance = v.get(i);
                            int status = hbaseInstance.getStatus();
                            if (status==0){
                                hbaseInstance.getHbaseOperations().closeConnection();
                                v.remove(hbaseInstance);
                            }
                        }
                    }
                });
            }
        }, 0, hbaseResourceProperties.getIdleTime(), TimeUnit.MINUTES);
    }
}
