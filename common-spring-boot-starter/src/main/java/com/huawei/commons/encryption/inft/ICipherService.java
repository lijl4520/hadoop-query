/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.encryption.inft;

public interface ICipherService {
    String encrypt(String var1, String var2);

    String decrypt(String var1, String var2);
}
