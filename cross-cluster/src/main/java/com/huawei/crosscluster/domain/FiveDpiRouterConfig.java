/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.crosscluster.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.huawei.commons.domain.AbstractRouterConfig;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author Lijl
 * @ClassName FiveDpiRouterConfig
 * @Description 5gDpi
 * @Date 2021/11/29 9:26
 * @Version 1.0
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FiveDpiRouterConfig extends AbstractRouterConfig {
    private String effectiveTime;
    private List<OldTimeIntervalProperties> oldTimeInterval = new LinkedList<>();
    private List<NewerTimeIntervalProperties> newerTimeInterval = new LinkedList<>();
}
