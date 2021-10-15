/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.domain.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Lijl
 * @ClassName RequestBody
 * @Description 请求参数实体
 * @Date 2021/9/7 17:31
 * @Version 1.0
 */
@Data
public class RestBodyEntity implements Serializable {
    private String msisdn;
    private String imsi;
    private String imei;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyyMMddHHmmss")
    @DateTimeFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyyMMddHHmmss")
    @DateTimeFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime endTime;
}
