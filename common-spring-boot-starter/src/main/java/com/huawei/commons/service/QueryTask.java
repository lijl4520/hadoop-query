/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.service;

import org.apache.hadoop.hbase.client.Result;

import java.util.List;
import java.util.Map;
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

    private HbaseOperations hbase;

    private RowMapper rowMapper;

    /**
     * 构造函数
     *
     * @param hbase       HBase查询工具类
     * @param tableName   HBase表名
     * @param startRowKey startRowKey
     * @param endRowKey   endRowKey
     */
    public QueryTask(HbaseOperations hbase, String tableName, String startRowKey,
                     String endRowKey,RowMapper rowMapper) {
        this.hbase = hbase;
        this.tableName = tableName;
        this.startRowKey = startRowKey;
        this.endRowKey = endRowKey;
        this.rowMapper = rowMapper;
    }

    @Override
    public Object call() {
        //List<Result> list = hbase.scanRowkeyRange(this.tableName, this.startRowKey, this.endRowKey);
        List<Map> list = hbase.find(this.tableName, this.startRowKey, this.endRowKey, this.rowMapper);
        return list;
    }
}
