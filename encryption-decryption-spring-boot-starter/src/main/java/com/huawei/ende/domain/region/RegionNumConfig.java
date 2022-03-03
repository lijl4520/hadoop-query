/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.ende.domain.region;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Lijl
 * @ClassName RegionNumConfig
 * @Description 算法装配
 * @Date 2022/3/2 13:59
 * @Version 1.0
 */
@Component
@Data
@ConfigurationProperties(prefix = "region")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RegionNumConfig {
    /**
     * dpi region number object
     */
    private DpiRegionNum dpiNum = new DpiRegionNum();
    /**
     * five dpi region number object
     */
    private FiveDpiRegionNum fiveDpiNum = new FiveDpiRegionNum();
    /**
     * five nx region number object
     */
    private FiveNxRegionNum fiveNxNum = new FiveNxRegionNum();
    /**
     * gn region number object
     */
    private GnRegionNum gnNum = new GnRegionNum();
    /**
     * ims region number object
     */
    private ImsRegionNum imsNum = new ImsRegionNum();
    /**
     * mme region number object
     */
    private MmeRegionNum mmeNum = new MmeRegionNum();
}
