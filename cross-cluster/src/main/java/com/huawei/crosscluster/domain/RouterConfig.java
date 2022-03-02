/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.crosscluster.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Lijl
 * @ClassName RouterConfig
 * @Description 路由配置
 * @Date 2021/11/25 10:57
 * @Version 1.0
 */
@Component
@Data
@ConfigurationProperties(prefix = "router-config")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RouterConfig {
    private ImsRouterConfig ims;
    private GnRouterConfig gn;
    private DpiRouterConfig dpi;
    private FiveDpiRouterConfig fiveDpi;
    private MmeRouterConfig mme;
    private FiveNxRouterConfig nx;
}
