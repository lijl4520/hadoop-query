/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.router;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * @Author Lijl
 * @ClassName OldTimeIntervalProperties
 * @Description TODO
 * @Date 2021/11/25 11:05
 * @Version 1.0
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OldTimeIntervalProperties extends AbstractRouter {
    private int startDate;
    private int endDate;
}
