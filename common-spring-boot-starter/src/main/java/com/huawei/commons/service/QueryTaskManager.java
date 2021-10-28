/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.service;

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

    public <T> List<T> query(HbaseOperations hbase, String filterVal, List<String> tableNameList,List<String> startAndEndRowKeyList){
        long startTime = System.currentTimeMillis();
        List query = query(hbase, filterVal, tableNameList, startAndEndRowKeyList.get(0), startAndEndRowKeyList.get(1));
        long endTime = System.currentTimeMillis();
        log.info("线程->{},查询耗时:{}秒",Thread.currentThread().getName(),(endTime-startTime)/1000);
        if (query!=null){
            if (query.size()>10000){
                Asserts.fail(ResultCode.DATA_EXCESS);
            }
        }else{
            Asserts.fail(ResultCode.FAILED);
        }
        return query;
    }

    private <T> List<T> query(HbaseOperations hbase, String filterVal, List<String> tableNameList, String startRowKey, String endRowKey) {
        log.info("=========>StartRowKey:{}/EndRowKey:{}",startRowKey,endRowKey);
        List resultList = new ArrayList<>();
        for (String tableName : tableNameList) {
            QueryTask queryTask = new QueryTask(hbase, tableName, startRowKey, endRowKey,filterVal,new MapRowMapper());
            try {
                Future future = executorService.submit(queryTask);
                List list = (List) future.get();
                resultList.addAll(list);
            } catch (InterruptedException e) {
                Asserts.fail(ResultCode.FAILED);
            } catch (ExecutionException e) {
                Asserts.fail(ResultCode.FAILED);
            }
        }
        return resultList;
    }
}
