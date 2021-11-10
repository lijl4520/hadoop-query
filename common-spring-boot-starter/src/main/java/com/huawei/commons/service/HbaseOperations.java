/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.service;

import org.apache.hadoop.hbase.client.Scan;

import java.util.List;

/**
 * @Author Lijl
 * @InterfaceName HbaseOperations
 * @Description hbase 基础操作
 * @Date 2021/10/25 14:30
 * @Version 1.0
 */
public interface HbaseOperations {

    /**
     * @Author lijiale
     * @MethodName execute
     * @Description 获取资源表
     * @Date 14:32 2021/10/25
     * @Version 1.0
     * @param tableName
     * @param action
     * @return: T
    **/
    <T> T execute(String tableName,TableCallback<T> action);


    <T> List<T> find(String tableName,String startRowKey,
               String endRowKey,String filterVal,final RowMapper<T> mapper);

    /**
     * @Author lijiale
     * @MethodName find
     * @Description 查询
     * @Date 15:29 2021/10/25
     * @Version 1.0
     * @param tableName
     * @param startRowKey
     * @param endRowKey
     * @param mapper
     * @return: java.util.List<T>
    **/
    <T> List<T> find(String tableName,String startRowKey,
                     String endRowKey,final RowMapper<T> mapper);


    /**
     * @Author lijiale
     * @MethodName find
     * @Description 查询
     * @Date 15:30 2021/10/25
     * @Version 1.0
     * @param tableName
     * @param scan
     * @param mapper
     * @return: java.util.List<T>
    **/
    <T> List<T> find(String tableName, final Scan scan, final RowMapper<T> mapper);

    HbaseOperations closeConnection();
}
