/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.service;

import com.huawei.commons.domain.HbaseInstance;

import java.util.concurrent.Callable;

/**
 * @Author Lijl
 * @ClassName QueryTask
 * @Description 线程任务
 * @Date 2021/10/15 10:23
 * @Version 1.0
 */
public class QueryTask implements Callable {

    private String tableName;

    private String startRowKey;

    private String endRowKey;

    private HbaseInstance hbase;

    private RowMapper rowMapper;

    private String filterVal;

    /**
     * 构造函数
     *
     * @param hbase       HBase查询工具类
     * @param tableName   HBase表名
     * @param startRowKey startRowKey
     * @param endRowKey   endRowKey
     */
    public QueryTask(HbaseInstance hbase, String tableName, String startRowKey,
                     String endRowKey, String filterVal, RowMapper rowMapper) {
        this.hbase = hbase;
        this.tableName = tableName;
        this.startRowKey = startRowKey;
        this.endRowKey = endRowKey;
        this.filterVal = filterVal;
        this.rowMapper = rowMapper;
    }

    @Override
    public Object call() {
        if (this.filterVal==null){
            return hbase.getHbaseOperations().find(this.tableName, this.startRowKey, this.endRowKey, this.rowMapper);
        }else{
            return hbase.getHbaseOperations().find(this.tableName, this.startRowKey, this.endRowKey, this.filterVal ,this.rowMapper);
        }
    }
}
