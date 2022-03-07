/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.router;

import java.util.List;

/**
 * @Author Lijl
 * @ClassName AbatractRouterConfig
 * @Description 抽象路由集合
 * @Date 2021/11/25 13:19
 * @Version 1.0
 */
public abstract class AbstractRouterConfig {
    public abstract String getEffectiveTime();
    public abstract List<? extends AbstractRouter> getOldTimeInterval();
    public abstract List<? extends AbstractRouter> getNewerTimeInterval();
}
