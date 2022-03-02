/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.ende.encryption;

import com.huawei.ende.domain.EncrypDecrypProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private MessageDigest digest;

    {
        try {
            digest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

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
            LocalDate now = LocalDate.now();
            DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd");
            String dateStr = now.format(ofPattern);
            if (Str!=null && !"".equals(Str)){
                long startTime = System.currentTimeMillis();
                String encrypt = Encrypter.encrypt(dateStr, Str, encrypDecrypProperties);
                long endTime = System.currentTimeMillis();
                log.info("数据加秘密耗时:{}",endTime-startTime);
                return encrypt;
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
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
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd");
            String dateStr = now.format(ofPattern);
            if (str!=null && !"".equals(str)){
                return Decrypter.decrypt(dateStr, str, encrypDecrypProperties);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * @Author lijiale
     * @MethodName createPrefix
     * @Description 生成rowkey前缀
     * @Date 10:29 2021/11/24
     * @Version 1.0
     * @param ciphertext
     * @return: long
    **/
    public String createPrefix(String ciphertext){
        byte[] secretBytes=digest.digest(ciphertext.getBytes());
        BigInteger bigInteger = new BigInteger(secretBytes);
        int i = bigInteger.mod(BigInteger.valueOf(10000L)).intValue();
        return StringUtils.leftPad(i + "", 4, "0");
    }

    /*public String createPrefix(String ciphertext){
        byte[] secretBytes = digest.digest(digest.digest(ciphertext.getBytes()));
        int prefixNum = (new BigInteger(secretBytes)).mod(BigInteger.valueOf(10000L)).intValue();
        String prefix = StringUtils.leftPad(prefixNum + "", 4, "0");
        return prefix;
    }*/
}
