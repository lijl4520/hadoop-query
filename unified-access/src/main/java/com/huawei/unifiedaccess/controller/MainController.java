/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.unifiedaccess.controller;

import com.alibaba.fastjson.JSONObject;
import com.huawei.commons.domain.resp.CommonResult;
import com.huawei.unifiedaccess.service.MainService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author Lijl
 * @ClassName MainController
 * @Description 请求入口
 * @Date 2021/11/23 13:52
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/v1/{model}")
public class MainController {

    private final MainService mainService;

    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    @PostMapping(value = "/{action}")
    public CommonResult action(HttpServletRequest request, @PathVariable String model, @PathVariable String action, @RequestBody JSONObject jsonObject){
        /*String timeStamp = request.getHeader("TimeStamp");
        String nonce = request.getHeader("nonce");
        String sign = request.getHeader("sign");
        if (!StringUtils.hasLength(timeStamp) ||
                !StringUtils.hasLength(nonce) ||
                !StringUtils.hasLength(sign)){
            return CommonResult.failed("Missing authentication parameters");
        }*/
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json;charset=utf-8");
        /*httpHeaders.add("TimeStamp", timeStamp);
        httpHeaders.add("nonce", nonce);
        httpHeaders.add("sign", sign);*/
        return mainService.doAction(model,action,jsonObject,httpHeaders);
    }
}
