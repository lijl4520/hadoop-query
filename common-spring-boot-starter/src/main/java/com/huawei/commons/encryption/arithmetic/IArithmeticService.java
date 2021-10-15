/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.encryption.arithmetic;

import org.apache.commons.codec.DecoderException;

public interface IArithmeticService {
    String encrypt(String var1, String var2);

    String decrypt(String var1, String var2) throws DecoderException;
}
