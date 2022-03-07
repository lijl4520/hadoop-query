/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.manager;

import com.huawei.commons.domain.HbaseInstance;
import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.exception.Asserts;
import com.huawei.commons.impl.ColumnIndexQueryTask;
import com.huawei.commons.impl.MapRowMapper;
import com.huawei.commons.impl.QueryTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        Map<String, HbaseInstance> hbaseInstance = hbaseManager.getHbaseInstance(filterVal);
        hbaseInstance.forEach((k,v)->{
            tableNameList.forEach(tableName ->{
                QueryTask queryTask = new QueryTask(v, tableName, startRowKey, endRowKey,filterVal,new MapRowMapper());
                try {
                    futureList.add(executorService.submit(queryTask));
                } catch (Exception e) {
                    Asserts.fail(ResultCode.FAILED);
                }
            });
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
        return resultList;
    }

    public <T> List<T> queryIndex(String family, String qualifier, Long startVal, Long endVal,
            Map<String,String> qualifierAndVal, String rowVal, String... tableNames){
        List<T> resultList = new ArrayList<>();
        List<Future<List<T>>> futureList = new ArrayList<>();
        for (String tableName : tableNames) {
            Map<String, HbaseInstance> hbaseInstance = hbaseManager.getHbaseInstance(rowVal);
            hbaseInstance.forEach((k,v)->{
                    ColumnIndexQueryTask columnIndexQueryTask = new ColumnIndexQueryTask(v, tableName, family, qualifier,
                            startVal, endVal, qualifierAndVal, rowVal, new MapRowMapper());
                    try {
                        futureList.add(executorService.submit(columnIndexQueryTask));
                    } catch (Exception e) {
                        Asserts.fail(ResultCode.FAILED);
                    }
            });
        }
        futureList.forEach(f ->{
            try {
                resultList.addAll(f.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        if (resultList.size()>10000){
            Asserts.fail(ResultCode.DATA_EXCESS);
        }
        return resultList;
    }
}
