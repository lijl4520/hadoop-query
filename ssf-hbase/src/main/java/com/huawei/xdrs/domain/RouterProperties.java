/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.xdrs.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author Lijl
 * @ClassName RouterProperties
 * @Description 路由参数配置
 * @Date 2021/10/18 15:37
 * @Version 1.0
 */
@Component
@Data
@ConfigurationProperties(prefix = "xdr.router-config")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RouterProperties {
    String effectiveTime;
    List<OldTimeIntervalProperties> oldTimeInterval = new LinkedList<>();
    List<NewerTimeIntervalProperties> newerTimeInterval = new LinkedList<>();
}

