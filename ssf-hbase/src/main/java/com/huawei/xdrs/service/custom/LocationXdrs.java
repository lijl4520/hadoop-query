/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.xdrs.service.custom;

import com.huawei.commons.domain.annotation.ActionService;
import com.huawei.commons.service.BaseService;
import com.huawei.commons.service.Hbase;
import com.huawei.commons.service.HbaseOperations;
import com.huawei.xdrs.domain.rest.RequestBodyEntity;
import com.huawei.xdrs.service.Xdrs;
import org.apache.hadoop.hbase.client.Result;

import java.util.List;
import java.util.Map;

/**
 * LocationXdrs
 * LocationXdr 查询
 * @since 2021/9/2
 */
@ActionService(value = "mme")
public class LocationXdrs extends Xdrs implements BaseService<RequestBodyEntity> {

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
        List<Result> resultList = super.queryTaskManager.query(hbase, tableNames, startAndEndRowKeys);
        List<Map<String, Object>> dataList = super.toMapList(resultList);
        return dataList;
    }
}
