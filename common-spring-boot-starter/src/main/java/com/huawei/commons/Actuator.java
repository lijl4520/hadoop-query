/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons;

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
     * @return: T
    **/
    <T> T execute(R r, QueryDataCallback<T> callback);
}
