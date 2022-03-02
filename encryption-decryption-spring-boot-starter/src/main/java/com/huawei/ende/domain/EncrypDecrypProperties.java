/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.ende.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author Lijl
 * @ClassName EncrypDecrypProperties
 * @Description 加解密rest地址参数
 * @Date 2021/10/14 17:23
 * @Version 1.0
 */
@ConfigurationProperties(prefix = "encryp-decryp")
public class EncrypDecrypProperties {
    private final boolean IS_HTTP = true;
    /**
     * SM4 secretKey address
     */
    private String url;
    /**
     * is http request
     */
    private boolean isHttp = IS_HTTP;
    /**
     * user id
     */
    private String userId;
    /**
     * keystore file path
     */
    private String keystoreFile;
    /**
     * keystore type
     */
    private String keystoreType;
    /**
     * keystore password
     */
    private String keystorePass;
    /**
     * truststore file
     */
    private String truststoreFile;
    /**
     * truststore type
     */
    private String truststoreType;
    /**
     * truststore pass
     */
    private String truststorePass;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHttp() {
        return isHttp;
    }

    public void setHttp(boolean http) {
        isHttp = http;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    public String getKeystoreType() {
        return keystoreType;
    }

    public void setKeystoreType(String keystoreType) {
        this.keystoreType = keystoreType;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    public String getTruststoreFile() {
        return truststoreFile;
    }

    public void setTruststoreFile(String truststoreFile) {
        this.truststoreFile = truststoreFile;
    }

    public String getTruststoreType() {
        return truststoreType;
    }

    public void setTruststoreType(String truststoreType) {
        this.truststoreType = truststoreType;
    }

    public String getTruststorePass() {
        return truststorePass;
    }

    public void setTruststorePass(String truststorePass) {
        this.truststorePass = truststorePass;
    }
}
