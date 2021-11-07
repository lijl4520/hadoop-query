/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.xdrs.service.custom;

import com.huawei.commons.domain.annotation.ActionService;
import com.huawei.commons.service.BaseService;
import com.huawei.commons.service.HbaseOperations;
import com.huawei.xdrs.domain.rest.RequestBodyEntity;
import com.huawei.xdrs.service.Xdrs;

import java.util.List;

/**
 * LocationXdrs
 * LocationXdr 查询
 * @since 2021/9/2
 */
@ActionService(value = "mme")
public class LocationXdrs extends Xdrs implements BaseService<Object,RequestBodyEntity> {

    /**
     * @Author Lijl
     * @MethodName query
     * @Description 查询
     * @Date 11:32 2021/9/8
     * @Version 1.0
     * @param requestBody 参数实体
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
    **/
    @Override
    public Object actionMethod(RequestBodyEntity requestBody) {
        List<String> tableNames = super.getTableNameList(requestBody, "S1MME");
        List<String> startAndEndRowKeys = super.getStartAndEndRowKeys(requestBody);
        HbaseOperations hbase = super.hbaseManager.getHbaseInstance();
        List resultList = super.queryTaskManager.query(hbase, requestBody.getProvince(), tableNames, startAndEndRowKeys);
        return resultList;
    }
}
