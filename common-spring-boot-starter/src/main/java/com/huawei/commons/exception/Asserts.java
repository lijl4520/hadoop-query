/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.exception;

import com.huawei.commons.domain.code.IErrorCode;

/**
 * @Author Lijl
 * @ClassName Asserts
 * @Description 断言处理类，用于抛出各种异常
 * @Date 2021/9/13 11:30
 * @Version 1.0
 */
public class Asserts {
    public static void fail(String message) {
        throw new QueryException(message);
    }

    public static void fail(IErrorCode errorCode) {
        throw new QueryException(errorCode);
    }
}
