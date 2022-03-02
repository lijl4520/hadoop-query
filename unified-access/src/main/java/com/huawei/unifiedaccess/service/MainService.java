/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.unifiedaccess.service;

import com.alibaba.fastjson.JSONObject;
import com.huawei.commons.domain.resp.CommonResult;
import org.springframework.http.HttpHeaders;

/**
 * @Author Lijl
 * @ClassName MainService
 * @Description TODO
 * @Date 2021/11/23 17:32
 * @Version 1.0
 */
public interface MainService {

    CommonResult doAction(String model, String action, JSONObject jsonObject, HttpHeaders httpHeaders);
}
