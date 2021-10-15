/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.controller;

import com.huawei.commons.domain.annotation.LoadPointcut;
import com.huawei.commons.domain.resp.CommonResult;
import com.huawei.querys.domain.rest.RestBodyEntity;
import com.huawei.querys.service.TwoThreeGQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Lijl
 * @ClassName TwoThreeGController
 * @Description 23G Hbase查询
 * @Date 2021/10/15 13:52
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/{version}")
@Slf4j
public class TwoThreeGController {

    private TwoThreeGQueryService twoThreeGQueryService;

    @Autowired
    public void setTwoThreeGQueryService(TwoThreeGQueryService twoThreeGQueryService) {
        this.twoThreeGQueryService = twoThreeGQueryService;
    }

    /**
     * @Author lijiale
     * @MethodName queryTwoThreeGHbaseData
     * @Description 查询23G hbase数据
     * @Date 14:01 2021/10/15
     * @Version 1.0
     * @param restBodyEntity
     * @return: com.huawei.commons.domain.resp.CommonResult
    **/
    @LoadPointcut
    @PostMapping(value = "/queryTwoThreeGHbaseData")
    public CommonResult queryTwoThreeGHbaseData(@RequestBody RestBodyEntity restBodyEntity){
        log.info("========> method queryTwoThreeGHbaseData requestBody:{}",restBodyEntity.toString());
        return CommonResult.success(twoThreeGQueryService.queryTwoThreeGData(restBodyEntity));
    }
}
