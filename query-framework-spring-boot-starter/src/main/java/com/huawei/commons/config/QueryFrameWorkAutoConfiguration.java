/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Lijl
 * @ClassName QueryFrameWorkAutoConfiguration
 * @Description 查询接口框架自动装配
 * @Date 2022/3/4 14:45
 * @Version 1.0
 */
@Configuration
@ComponentScan("com.huawei")
@MapperScan("com.huawei.router.mapper")
public class QueryFrameWorkAutoConfiguration {
}
