/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.service;

import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.exception.Asserts;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * QueryTaskManager
 *
 * @since 2021/9/7
 */
@Slf4j
@Component
public class QueryTaskManager {

    private ThreadPoolTaskExecutor executorService;

    @Autowired
    public void setExecutorService(ThreadPoolTaskExecutor executorService) {
        this.executorService = executorService;
    }

    final static String HBASE_USER_NAME="DETAIL_HW:";


    /**
     * 查询HBase多张表，使用多个线程执行
     * @param hbase         hbase查询工具类
     * @param tableNameList HBase表集合
     * @param startRowKey   startRowKey
     * @param endRowKey     endRowKey
     * @return 多张表合并结果集
     */
    private List<Result> query(Hbase hbase, List<String> tableNameList, String startRowKey, String endRowKey) {
        log.info("=========>StartRowKey:{}/EndRowKey:{}",startRowKey,endRowKey);
        List<Result> resultList = new ArrayList<>();
        for (String tableName : tableNameList) {
            QueryTask queryTask = new QueryTask(hbase, HBASE_USER_NAME+tableName, startRowKey, endRowKey);
            try {
                Future future = executorService.submit(queryTask);
                List<Result> list = (List<Result>) future.get();
                resultList.addAll(list);
            } catch (InterruptedException e) {
                Asserts.fail(ResultCode.FAILED);
            } catch (ExecutionException e) {
                Asserts.fail(ResultCode.FAILED);
            }
        }
        return resultList;
    }

    /**
     * @Author lijiale
     * @MethodName query
     * @Description 查询
     * @Date 9:44 2021/10/15
     * @Version 1.0
     * @param hbase
     * @param tableNameList
     * @param startAndEndRowKeyList
     * @return: java.util.List<org.apache.hadoop.hbase.client.Result>
    **/
    public List<Result> query(Hbase hbase, List<String> tableNameList,List<String> startAndEndRowKeyList){
        long startTime = System.currentTimeMillis();
        List<Result> query = query(hbase, tableNameList, startAndEndRowKeyList.get(0), startAndEndRowKeyList.get(1));
        long endTime = System.currentTimeMillis();
        log.info("线程->{},查询耗时:{}秒",Thread.currentThread().getName(),(endTime-startTime)/1000);
        if (query!=null){
            if (query.size()>10000){
                Asserts.fail(ResultCode.DATA_EXCESS);
            }
        }
        return null;
    }
}
