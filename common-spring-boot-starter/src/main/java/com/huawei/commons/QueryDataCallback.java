/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons;

import java.util.List;
import java.util.Map;

/**
 * @Author Lijl
 * @ClassName QueryDataCallback
 * @Description 查询结果回调
 * @Date 2021/11/10 14:55
 * @Version 1.0
 */
@FunctionalInterface
public interface QueryDataCallback<T> {

    T doInData(String province,List<String> tableNameList,List<String> startAndEndRowKeys);
}
