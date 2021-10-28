/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.service.impl;

import com.huawei.commons.domain.annotation.ActionService;
import com.huawei.commons.service.*;
import com.huawei.querys.domain.rest.RestBodyEntity;
import com.huawei.querys.service.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * @Author Lijl
 * @ClassName TwoThreeGQueryServiceImpl
 * @Description 23G查询
 * @Date 2021/10/14 16:52
 * @Version 1.0
 */
@ActionService(value = "queryTwoThreeG")
public class TwoThreeGQueryServiceImpl extends AbstractService implements BaseService<Object,RestBodyEntity> {

    private HbaseManager hbaseManager;

    @Autowired
    public void setHbaseManager(HbaseManager hbaseManager) {
        this.hbaseManager = hbaseManager;
    }
    private QueryTaskManager queryTaskManager;

    @Autowired
    public void setQueryTaskManager(QueryTaskManager queryTaskManager) {
        this.queryTaskManager = queryTaskManager;
    }

    @Override
    public Object actionMethod(RestBodyEntity restBodyEntity) {
        List<String> tableNameList = super.getTableNameList(restBodyEntity,"GN");
        List<String> startAndEndRowKeys = super.getStartAndEndRowKeys(restBodyEntity);
        HbaseOperations hbase = this.hbaseManager.getHbaseInstance();
        List resultList = this.queryTaskManager.query(hbase,null, tableNameList, startAndEndRowKeys);
        return resultList;
    }
}
