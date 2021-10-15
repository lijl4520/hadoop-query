/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.service;

import com.huawei.querys.domain.rest.RestBodyEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author Lijl
 * @InterfaceName TwoThreeGQueryService
 * @Description 23G 数据查询接口
 * @Date 2021/10/14 16:51
 * @Version 1.0
 */
public interface TwoThreeGQueryService {

    List<Map<String,Object>> queryTwoThreeGData(RestBodyEntity restBodyEntity);
}
