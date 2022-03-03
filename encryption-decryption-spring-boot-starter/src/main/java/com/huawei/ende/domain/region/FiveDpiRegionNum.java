/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.ende.domain.region;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Lijl
 * @ClassName FiveDpiReginNum
 * @Description 5G DPI Region 算法
 * @Date 2022/3/2 13:47
 * @Version 1.0
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FiveDpiRegionNum extends AbstractRegionNum{
    private final int REGION_NUMBER = 1000;
    @Getter
    @Setter
    private int regionNum = REGION_NUMBER;
}
