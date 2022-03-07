/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons;

import java.util.Map;

/**
 * @Author Lijl
 * @ClassName QueryIndexDataCallback
 * @Description TODO
 * @Date 2021/12/24 15:00
 * @Version 1.0
 */
@FunctionalInterface
public interface QueryIndexDataCallback<T> {

    T doInData(String family, String qualifier, Long startVal, Long endVal,
               Map<String,String> qualifierAndVal, String rowVal, String... tableNames);
}
