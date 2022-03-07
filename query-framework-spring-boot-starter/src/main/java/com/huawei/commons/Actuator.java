/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons;

import com.huawei.router.AbstractRouterConfig;

/**
 * @Author Lijl
 * @InterfaceName BusinessExcute
 * @Description 业务执行接口
 * @Date 2021/11/11 9:36
 * @Version 1.0
 */
public interface Actuator<R> {

    /**
     * @Author lijiale
     * @MethodName excute
     * @Description 执行业务
     * @Date 9:44 2021/11/11
     * @Version 1.0
     * @param r
     * @param callback
     * @param prefix
     * @param databaseName
     * @param routerConfig
     * @param regionNum
     * @return: T
    **/
    <T> T execute(R r, QueryDataCallback<T> callback, String prefix, String databaseName, AbstractRouterConfig routerConfig, int regionNum);

    /**
     * @Author lijiale
     * @MethodName executeIndex
     * @Description 执行二级索引业务
     * @Date 9:44 2021/11/11
     * @Version 1.0
     * @param r
     * @param callback
     * @param prefix
     * @param databaseName
     * @param routerConfig
     * @return: T
     **/
    <T> T executeIndex(R r, QueryIndexDataCallback<T> callback, String prefix, String databaseName, AbstractRouterConfig routerConfig);
}
