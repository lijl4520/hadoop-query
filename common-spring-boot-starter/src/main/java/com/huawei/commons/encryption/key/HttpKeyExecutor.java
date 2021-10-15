/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.encryption.key;

import com.huawei.commons.encryption.util.HttpRequester;
import com.huawei.commons.encryption.util.HttpRespons;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpKeyExecutor implements IKeyExecutor {
    private String adapterUrl;

    public HttpKeyExecutor(String adapterUrl) {
        this.adapterUrl = adapterUrl;
    }

    @Override
    public String getHttpKey(String requestTime) {
        HttpRequester request = new HttpRequester();
        String key = "";
        Map<String, String> params = new HashMap();
        params.put("requestTime", requestTime);
        try {
            HttpRespons hr = request.sendGet(this.adapterUrl, params);
            String content = hr.getContent();
            return content;
        } catch (IOException var7) {
            log.error("connect adapter failed!!");
            log.error(var7.getMessage(), var7);
            return key;
        }
    }
}
