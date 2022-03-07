/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.impl;

import com.huawei.commons.ScanCallback;
import com.huawei.commons.TableCallback;
import org.apache.hadoop.hbase.client.Scan;

import java.util.List;
import java.util.Map;

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
    <T> T execute(String tableName, TableCallback<T> action);

    <T> T execute(String tableName, Scan scan, ScanCallback<T> action);


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
     * @Description 二级索引查询
     * @Date 16:31 2021/11/22
     * @Version 1.0
     * @param tableName 表名
     * @param family 列簇
     * @param qualifier 区间查询列
     * @param startVal 区间开始值
     * @param endVal 区间结束值
     * @param qualifierAndVal 二级索引查询列-值
     * @param rowVal 省份查询值
     * @param mapper
     * @return: java.util.List<T>
    **/
    <T> List<T> find(String tableName, String family, String qualifier, Long startVal,
                     Long endVal, Map<String,String> qualifierAndVal, String rowVal, final RowMapper<T> mapper);

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
