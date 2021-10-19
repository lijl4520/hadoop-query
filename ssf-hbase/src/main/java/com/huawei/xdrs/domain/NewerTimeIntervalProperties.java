/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.xdrs.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Lijl
 * @ClassName NewerTimeIntervalProperties
 * @Description 路由配置-新时段配置参数
 * @Date 2021/10/18 15:35
 * @Version 1.0
 */
@Component
@Data
@ConfigurationProperties(prefix = "xdr.router-config.newer-time-interval")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NewerTimeIntervalProperties implements BaseProperties{
    private int startDate;
    private int endDate;
}
