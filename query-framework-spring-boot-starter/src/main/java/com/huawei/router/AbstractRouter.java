/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.router;

/**
 * @Author Lijl
 * @InterfaceName BaseRouter
 * @Description 获取路由规则基类
 * @Date 2021/11/25 11:10
 * @Version 1.0
 */
public abstract class AbstractRouter {
    public abstract int getStartDate();
    public abstract int getEndDate();
}
