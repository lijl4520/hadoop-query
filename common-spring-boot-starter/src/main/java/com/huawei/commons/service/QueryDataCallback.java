/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.service;

import java.util.List;

/**
 * @Author Lijl
 * @ClassName QueryDataCallback
 * @Description TODO
 * @Date 2021/11/10 14:55
 * @Version 1.0
 */
public interface QueryDataCallback<T> {

    T doInData(String province,List<String> tableNameList,List<String> startAndEndRowKeys);
}
