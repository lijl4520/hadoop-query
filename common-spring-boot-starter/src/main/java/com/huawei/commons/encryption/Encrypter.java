/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.encryption;



import com.huawei.commons.encryption.inft.CipherServiceImpl;
import com.huawei.commons.encryption.inft.ICipherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class Encrypter {
    private static ICipherService cipher;

    public Encrypter() {
    }

    public static String encrypt(String paramDate, String number, String authSrvUrl) {
        if (cipher == null) {
            cipher = new CipherServiceImpl(authSrvUrl, Integer.parseInt(paramDate));
        }

        String v = cipher.encrypt(paramDate, number);
        if (StringUtils.isBlank(v)) {
            log.warn("get key value failed,please check paramter");
        }

        return v;
    }
}
