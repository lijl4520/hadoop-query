/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.encryption;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpKeyExecutor {
    private String adapterUrl;

    public HttpKeyExecutor(String adapterUrl) {
        this.adapterUrl = adapterUrl;
    }

    public String getHttpKey(String requestTime) {
        HttpRequester request = new HttpRequester();
        String key = "";
        Map<String, String> params = new HashMap();
        params.put("requestTime", requestTime);

        try {
            key = request.send(this.adapterUrl + "?requestTime=" + requestTime + "01000000").split(",")[0];
            return key;
        } catch (IOException var6) {
            log.error(var6.toString());
            return key;
        }
    }
}
