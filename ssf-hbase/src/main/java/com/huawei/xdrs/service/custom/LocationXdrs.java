/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.xdrs.service.custom;

import com.huawei.commons.domain.annotation.ActionService;
import com.huawei.commons.manager.QueryTaskManager;
import com.huawei.commons.BaseService;
import com.huawei.xdrs.domain.rest.RequestBodyEntity;
import com.huawei.xdrs.service.Xdrs;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * LocationXdrs
 * LocationXdr 查询
 * @since 2021/9/2
 */
@ActionService(value = "mme")
public class LocationXdrs extends Xdrs implements BaseService<Object,RequestBodyEntity> {

    private QueryTaskManager queryTaskManager;

    @Autowired
    public void setQueryTaskManager(QueryTaskManager queryTaskManager) {
        this.queryTaskManager = queryTaskManager;
    }

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
        return super.execute(requestBody,(province, tableNameList, startAndEndRowKeys) -> queryTaskManager.query(province, tableNameList, startAndEndRowKeys),"S1MME","DETAIL_HW:",null);
    }
}
