/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;

/**
 * @Author Lijl
 * @InterfaceName ScanCallback
 * @Description TODO
 * @Date 2022/2/13 9:21
 * @Version 1.0
 */
public interface ScanCallback<T> {

    /**
     * @Author lijiale
     * @MethodName doInTable
     * @Description TODO
     * @Date 10:16 2022/2/13
     * @Version 1.0
     * @param connection
 * @param table
 * @param scan
     * @return: T
    **/
    T doInTable(Connection connection, Table table, Scan scan) throws Exception;
}
