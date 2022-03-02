/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.crosscluster.domain.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Author Lijl
 * @ClassName RestEntity
 * @Description 入参
 * @Date 2021/11/19 16:36
 * @Version 1.0
 */
@Data
public class RestBodyEntity {
    private String msisdn;
    private String imsi;
    private String imei;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyyMMddHHmmss")
    @DateTimeFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyyMMddHHmmss")
    @DateTimeFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime endTime;
    private String province;
    private String city;
}
