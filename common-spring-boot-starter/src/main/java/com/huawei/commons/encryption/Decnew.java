/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.encryption;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class Decnew {
    private static boolean getkey = false;
    private static String deckey = "";
    private static SM4 sm4 = new SM4();

    public Decnew() {
    }

    public static String decnew(String date, String decstr, String authSrvUrl, String type) {
        if (!getkey) {
            Class var4 = Decnew.class;
            synchronized(Decnew.class) {
                HttpKeyExecutor hke = new HttpKeyExecutor(authSrvUrl);
                deckey = hke.getHttpKey(date);
                sm4.set(type);
                getkey = true;
            }
        }

        String v = "";

        try {
            v = sm4.decrypt(decstr, deckey);
            if (StringUtils.isBlank(v)) {
                log.warn("decrypt ciphertext failed,please check paramter");
                return v;
            }
        } catch (DecoderException var6) {
            log.error(var6.toString());
        }

        return v;
    }
}
