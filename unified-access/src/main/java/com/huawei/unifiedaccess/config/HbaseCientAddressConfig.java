/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.unifiedaccess.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author Lijl
 * @ClassName HbaseCientAddressConfig
 * @Description TODO
 * @Date 2021/11/23 16:57
 * @Version 1.0
 */
@Component
@ConfigurationProperties(prefix = "hbase-client")
@Data
public class HbaseCientAddressConfig {
    private Map<String,String> address;
}
