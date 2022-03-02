/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.domain.code;

/**
 * @Author Lijl
 * @ClassName ResultCode
 * @Description 枚举了一些常用API操作码
 * @Date 2021/9/13 10:48
 * @Version 1.0
 */
public enum ResultCode implements IErrorCode {

    SUCCESS(0,"成功"),
    VALIDATE_FAILED(1,"参数有误"),
    DATA_EXCESS(2,"数据超限"),
    ENCRYP_DECRYP(3,"密钥中心加密失败"),
    OUT_TIME(4,"查询时间超出范围"),
    FAILED(5,"其它错误"),
    PART_SUCCESS(6,"部分成功");

    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
