/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */
package com.huawei.commons.encryption;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class Encnew {
    private static boolean getkey = false;
    private static String enckey = "";
    private static SM4 sm4 = new SM4();

    public Encnew() {
    }

    public static String encnew(String date, String encstr, String authSrvUrl, String type) {
        if (!getkey) {
            Class var4 = Encnew.class;
            synchronized(Encnew.class) {
                HttpKeyExecutor hke = new HttpKeyExecutor(authSrvUrl);
                enckey = hke.getHttpKey(date);
                sm4.set(type);
                getkey = true;
            }
        }

        String v = "";
        v = sm4.encrypt(encstr, enckey);
        if (StringUtils.isBlank(v)) {
            log.warn("encrypt ciphertext failed,please check paramter");
            return v;
        } else {
            return v.toUpperCase();
        }
    }
}
