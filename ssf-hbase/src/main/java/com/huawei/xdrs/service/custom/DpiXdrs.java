/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.xdrs.service.custom;

import com.huawei.commons.domain.annotation.ActionService;
import com.huawei.commons.BaseService;
import com.huawei.xdrs.domain.rest.RequestBodyEntity;
import com.huawei.xdrs.service.Xdrs;


/**
 * DpiXdrs
 *
 * @since 2021/9/2
 */
@ActionService(value = "dpi")
public class DpiXdrs extends Xdrs implements BaseService<Object,RequestBodyEntity> {

    @Override
    public Object actionMethod(RequestBodyEntity requestBodyEntity) {
        return null;
    }
}
