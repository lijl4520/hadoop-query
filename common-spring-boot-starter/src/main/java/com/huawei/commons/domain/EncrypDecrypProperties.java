/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.domain;

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
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
