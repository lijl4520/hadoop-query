/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons;

import org.apache.hadoop.hbase.client.Table;

/**
 * @Author Lijl
 * @InterfaceName TableCallback
 * @Description Hbase 代码回调接口，作为方法实现中的匿名类
 * @Date 2021/10/25 14:26
 * @Version 1.0
 */
public interface TableCallback<T> {

    /**
     * @Author lijiale
     * @MethodName doInTable
     * @Description 使用活动的habse表
     * @Date 14:29 2021/10/25
     * @Version 1.0
     * @param table
     * @throws Exception
     * @return: T
    **/
    T doInTable(Table table) throws Exception;
}
