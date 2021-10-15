/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.domain.code;

/**
 * @Author Lijl
 * @InterfaceName IErrorCode
 * @Description 异常基类接口
 * @Date 2021/9/13 11:08
 * @Version 1.0
 */
public interface IErrorCode {

    long getCode();

    String getMessage();
}
