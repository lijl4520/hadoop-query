/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.service.impl;

import com.huawei.commons.BaseService;
import com.huawei.commons.domain.annotation.ActionService;
import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.exception.QueryException;
import com.huawei.commons.manager.QueryTaskManager;
import com.huawei.querys.domain.rest.RestBodyEntity;
import com.huawei.querys.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author Lijl
 * @ClassName TwoThreeGQueryServiceImpl
 * @Description 23G查询
 * @Date 2021/10/14 16:52
 * @Version 1.0
 */
@ActionService(value = "gn")
public class TwoThreeGQueryServiceImpl extends AbstractService implements BaseService<Object,RestBodyEntity> {

    private QueryTaskManager queryTaskManager;

    @Autowired
    public void setQueryTaskManager(QueryTaskManager queryTaskManager) {
        this.queryTaskManager = queryTaskManager;
    }

    @Override
    public Object actionMethod(RestBodyEntity restBodyEntity) {
        String msisdn = restBodyEntity.getMsisdn();
        String imsi = restBodyEntity.getImsi();
        String imei = restBodyEntity.getImei();
        if (StringUtils.hasLength(msisdn)){
            return super.execute(restBodyEntity,
                    (province, tableNameList, startAndEndRowKeys) -> super.mappingField(queryTaskManager.query(province, tableNameList, startAndEndRowKeys))

                    ,"GN","DWA_HW:",null);
        }else if (StringUtils.hasLength(imsi)||StringUtils.hasLength(imei)){
            return super.executeIndex(restBodyEntity,
                    (family, qualifier, startVal, endVal,qualifierAndVal, rowVal, tableNames)->super.mappingField(queryTaskManager.queryIndex(family,qualifier,startVal,endVal,qualifierAndVal,rowVal,tableNames)),
                    "GN","DWA_HW:",null);
        }
        throw new QueryException(ResultCode.VALIDATE_FAILED);
    }
}
