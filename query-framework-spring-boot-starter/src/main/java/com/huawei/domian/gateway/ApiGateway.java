/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.domian.gateway;

import com.alibaba.fastjson.JSONObject;
import com.huawei.commons.annotation.LoadPointcut;
import com.huawei.commons.domain.resp.CommonResult;
import com.huawei.commons.util.JsonToMap;
import com.huawei.commons.util.SignUtil;
import com.huawei.domian.manager.BaseServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
     * @param json 接口参数
     * @return: com.huawei.commons.domain.resp.CommonResult
    **/
    @LoadPointcut
    @PostMapping(value = "/{action}")
    public CommonResult unifiedInlet(@PathVariable String version, @PathVariable String action, @RequestBody JSONObject json){
        log.info("========> server {} method {} requestBody:{}",version,action,json.toJSONString());
        return CommonResult.success(new BaseServiceManager
                .Builder()
                .action(action)
                .object(json)
                .build());
    }

    /**
     * @Author lijiale
     * @MethodName generateSign
     * @Description 生成AK
     * @Date 16:34 2021/11/11
     * @Version 1.0
     * @param json
     * @return: com.huawei.commons.domain.resp.CommonResult
    **/
    @PostMapping(value = "/sign")
    public CommonResult generateSign(@RequestBody JSONObject json){
        Map sortedMap = JsonToMap.sortParams(json);
        return CommonResult.success(SignUtil.wrapperHeader(sortedMap));
    }
}
