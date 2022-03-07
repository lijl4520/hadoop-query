/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons;

/**
 * @Author Lijl
 * @InterfaceName BaseService
 * @Description 服务底层基类
 * @Date 2021/10/19 14:45
 * @Version 1.0
 */
public interface BaseService<T,R> {

    /**
     * @Author lijiale
     * @MethodName actionMethod
     * @Description 通用业务方法
     * @Date 15:16 2021/10/19
     * @Version 1.0
     * @param r
     * @return: java.lang.Object
    **/
    T actionMethod(R r);
}
