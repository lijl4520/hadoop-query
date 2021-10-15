/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.exception;


import com.huawei.commons.domain.code.IErrorCode;

/**
 * @Author Lijl
 * @ClassName XdrException
 * @Description 自定义API异常
 * @Date 2021/9/13 10:42
 * @Version 1.0
 */
public class QueryException extends RuntimeException{
    private IErrorCode errorCode;

    public QueryException(IErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public QueryException(String message) {
        super(message);
    }

    public QueryException(Throwable cause) {
        super(cause);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}
