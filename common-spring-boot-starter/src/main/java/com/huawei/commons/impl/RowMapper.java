/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.impl;

import org.apache.hadoop.hbase.client.Result;

/**
 * @Author Lijl
 * @InterfaceName RowMapper
 * @Description 数据映射
 * @Date 2021/10/25 15:26
 * @Version 1.0
 */
public interface RowMapper<T> {

    /**
     * @Author lijiale
     * @MethodName mapRow
     * @Description 数据映射
     * @Date 15:28 2021/10/25
     * @Version 1.0
     * @param result
     * @return: T
    **/
    T mapRow(Result result) throws Exception;
}
