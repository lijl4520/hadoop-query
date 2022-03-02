/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.ende.encryption;

import com.huawei.ende.domain.EncrypDecrypProperties;
import com.huawei.ende.encryption.inft.CipherServiceImpl;
import com.huawei.ende.encryption.inft.ICipherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author Lijl
 * @ClassName Decrypter
 * @Description TODO
 * @Date 2021/12/1 11:27
 * @Version 1.0
 */
@Slf4j
public class Decrypter {
    private static ICipherService cipher;

    public static String decrypt(String paramDate, String number, EncrypDecrypProperties authProperties) {
        if (cipher == null) {
            cipher = new CipherServiceImpl(authProperties, paramDate);
        }

        String v = cipher.decrypt(paramDate, number);
        if (StringUtils.isBlank(v)) {
            log.warn("get key value failed,please check paramter");
        }

        return v;
    }
}
