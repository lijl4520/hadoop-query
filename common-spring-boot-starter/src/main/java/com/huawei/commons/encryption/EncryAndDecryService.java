/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.encryption;

import com.huawei.commons.domain.EncrypDecrypProperties;
import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.encryption.Decnew;
import com.huawei.commons.encryption.Encrypter;
import com.huawei.commons.exception.Asserts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @Author Lijl
 * @ClassName EncryAndDecryService
 * @Description 加解密
 * @Date 2021/10/15 11:31
 * @Version 1.0
 */
@Slf4j
@Component
@EnableConfigurationProperties(EncrypDecrypProperties.class)
public class EncryAndDecryService {

    private EncrypDecrypProperties encrypDecrypProperties;

    @Autowired
    public void setEncrypDecrypProperties(EncrypDecrypProperties encrypDecrypProperties) {
        this.encrypDecrypProperties = encrypDecrypProperties;
    }

    /**
     * @Author lijiale
     * @MethodName sm4Ecnew
     * @Description SM4加密
     * @Date 13:42 2021/10/15
     * @Version 1.0
     * @param Str
     * @return: java.lang.String
    **/
    public String sm4Ecnew(String Str) {
        try {
            if (Str!=null && !"".equals(Str)){
                LocalDate now = LocalDate.now();
                DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd");
                String dateStr = now.format(ofPattern);
                long startTime = System.currentTimeMillis();
                String encrypt = Encrypter.encrypt(dateStr, Str, encrypDecrypProperties.getUrl());
                long endTime = System.currentTimeMillis();
                log.info("数据加秘密耗时:{}",endTime-startTime);
                return encrypt;
            }
        }catch (Exception e){
            Asserts.fail(ResultCode.ENCRYP_DECRYP);
        }
        return null;
    }


    /**
     * @Author lijiale
     * @MethodName sm4Decryp
     * @Description SM4解密
     * @Date 13:42 2021/10/15
     * @Version 1.0
     * @param str
     * @return: java.lang.String
    **/
    public String sm4Decryp(String str){
        try {
            if (str!=null && !"".equals(str)){
                LocalDate now = LocalDate.now();
                DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd");
                String dateStr = now.format(ofPattern);
                return Decnew.decnew(dateStr, str, encrypDecrypProperties.getUrl(), "utf-8");
            }
        }catch (Exception e){
            Asserts.fail(ResultCode.ENCRYP_DECRYP);
        }
        return null;
    }
}
