/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.xdrs.domain.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huawei.commons.domain.rest.BaseEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Lijl
 * @ClassName RequestBodyEntity
 * @Description TODO
 * @Date 2021/10/18 15:33
 * @Version 1.0
 */
@Data
public class RequestBodyEntity implements BaseEntity {
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
