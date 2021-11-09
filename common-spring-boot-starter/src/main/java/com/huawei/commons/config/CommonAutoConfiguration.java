/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Lijl
 * @ClassName CommonAutoConfiguration
 * @Description 自动装载扫描配置
 * @Date 2021/10/14 13:57
 * @Version 1.0
 */
@Configuration
@ComponentScan("com.huawei.commons")
public class CommonAutoConfiguration {
}
