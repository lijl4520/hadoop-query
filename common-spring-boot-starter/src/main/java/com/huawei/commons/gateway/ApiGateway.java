/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.gateway;

import com.alibaba.fastjson.JSONObject;
import com.huawei.commons.manager.BaseServiceManager;
import com.huawei.commons.domain.annotation.LoadPointcut;
import com.huawei.commons.domain.resp.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Lijl
 * @ClassName ApiGateway
 * @Description api统一入口
 * @Date 2021/10/19 14:39
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/{version}")
@Slf4j
public class ApiGateway {

    /**
     * @Author lijiale
     * @MethodName unifiedInlet
     * @Description API统一入口
     * @Date 17:28 2021/10/19
     * @Version 1.0
     * @param version 服务版本号
     * @param action API服务名
     * @param jsonObject 接口参数
     * @return: com.huawei.commons.domain.resp.CommonResult
    **/
    @LoadPointcut
    @PostMapping(value = "/{action}")
    public CommonResult unifiedInlet(@PathVariable String version,@PathVariable String action, @RequestBody JSONObject jsonObject){
        log.info("========> server {} method {} requestBody:{}",version,action,jsonObject.toJSONString());
        return CommonResult.success(new BaseServiceManager
                .Builder()
                .action(action)
                .object(jsonObject)
                .build());
    }
}
