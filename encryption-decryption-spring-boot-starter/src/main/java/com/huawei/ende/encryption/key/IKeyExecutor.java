/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.ende.encryption.key;

import java.io.IOException;

public interface IKeyExecutor {
    String getHttpKey(String var1) throws IOException;
}
