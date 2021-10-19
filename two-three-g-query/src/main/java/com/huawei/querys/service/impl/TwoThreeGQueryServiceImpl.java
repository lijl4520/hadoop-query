/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.service.impl;

import com.huawei.commons.domain.annotation.ActionService;
import com.huawei.commons.service.BaseService;
import com.huawei.commons.service.Hbase;
import com.huawei.commons.service.HbaseManager;
import com.huawei.commons.service.QueryTaskManager;
import com.huawei.querys.domain.rest.RestBodyEntity;
import com.huawei.querys.service.*;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @Author Lijl
 * @ClassName TwoThreeGQueryServiceImpl
 * @Description 23G查询
 * @Date 2021/10/14 16:52
 * @Version 1.0
 */
@ActionService(value = "queryTwoThreeG")
public class TwoThreeGQueryServiceImpl extends AbstractService implements BaseService<RestBodyEntity> {

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
        Hbase hbase = this.hbaseManager.getHbaseInstance();
        List<Result> resultList = this.queryTaskManager.query(hbase, tableNameList, startAndEndRowKeys);
        return super.toMapList(resultList);
    }
}
