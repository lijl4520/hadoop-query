/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.crosscluster.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.huawei.commons.domain.AbstractRouter;
import lombok.Data;

/**
 * @Author Lijl
 * @ClassName NewerTimeIntervalProperties
 * @Description TODO
 * @Date 2021/11/25 11:02
 * @Version 1.0
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NewerTimeIntervalProperties extends AbstractRouter {
    private int startDate;
    private int endDate;
}
