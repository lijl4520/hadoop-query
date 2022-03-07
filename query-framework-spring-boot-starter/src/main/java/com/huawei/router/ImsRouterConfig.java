/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.router;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author Lijl
 * @ClassName ImsRouterConfig
 * @Description IMS
 * @Date 2021/11/25 10:58
 * @Version 1.0
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ImsRouterConfig extends AbstractRouterConfig {
    private String effectiveTime;
    private List<OldTimeIntervalProperties> oldTimeInterval = new LinkedList<>();
    private List<NewerTimeIntervalProperties> newerTimeInterval = new LinkedList<>();
}
