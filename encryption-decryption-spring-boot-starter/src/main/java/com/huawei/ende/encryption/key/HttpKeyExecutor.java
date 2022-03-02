/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.ende.encryption.key;

import com.huawei.ende.domain.EncrypDecrypProperties;
import com.huawei.ende.encryption.util.HttpRequester;
import com.huawei.ende.encryption.util.HttpRespons;
import com.huawei.ende.encryption.webservice.WebServiceClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpKeyExecutor implements IKeyExecutor {
    private EncrypDecrypProperties authProperties;

    public HttpKeyExecutor(EncrypDecrypProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public String getHttpKey(String requestTime) throws IOException {
        if (authProperties.isHttp()){
            HttpRequester request = new HttpRequester();
            Map<String, String> params = new HashMap(1);
            params.put("requestTime", requestTime);
            HttpRespons hr = request.sendGet(this.authProperties.getUrl(), params);
            String content = hr.getContent();
            return content;

        }else{
            return WebServiceClient.getSecretKeyByUser(this.authProperties,requestTime);
        }
    }
}
