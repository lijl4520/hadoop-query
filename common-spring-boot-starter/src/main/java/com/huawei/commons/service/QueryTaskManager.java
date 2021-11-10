/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.service;

import com.huawei.commons.domain.HbaseInstance;
import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.exception.Asserts;
import lombok.extern.slf4j.Slf4j;
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

    private HbaseManager hbaseManager;

    @Autowired
    public void setHbaseManager(HbaseManager hbaseManager) {
        this.hbaseManager = hbaseManager;
    }

    public <T> List<T> query(String filterVal, List<String> tableNameList, List<String> startAndEndRowKeyList){
        List list = query(filterVal, tableNameList, startAndEndRowKeyList.get(0), startAndEndRowKeyList.get(1));
        if (list!=null){
            if (list.size()>10000){
                Asserts.fail(ResultCode.DATA_EXCESS);
            }
        }else{
            Asserts.fail("查询结果错误");
        }
        return list;
    }

    private <T> List<T> query(String filterVal, List<String> tableNameList, String startRowKey, String endRowKey) {
        log.info("=========>StartRowKey:{}/EndRowKey:{}",startRowKey,endRowKey);
        List<T> resultList = new ArrayList<>();
        List<Future<List<T>>> futureList = new ArrayList<>();
        HbaseInstance hbaseInstance = hbaseManager.getHbaseInstance();
        tableNameList.forEach(tableName ->{
            QueryTask queryTask = new QueryTask(hbaseInstance, tableName, startRowKey, endRowKey,filterVal,new MapRowMapper());
            try {
                futureList.add(executorService.submit(queryTask));
            } catch (Exception e) {
                Asserts.fail(ResultCode.FAILED);
            }
        });
        futureList.forEach(f ->{
            try {
                resultList.addAll(f.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        hbaseInstance.setStatus(0);
        return resultList;
    }
}
