/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.crosscluster.service.impl;

import com.huawei.commons.BaseService;
import com.huawei.commons.domain.annotation.ActionService;
import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.exception.QueryException;
import com.huawei.commons.manager.QueryTaskManager;
import com.huawei.crosscluster.domain.RouterConfig;
import com.huawei.crosscluster.domain.rest.RestBodyEntity;
import com.huawei.crosscluster.service.AbstractCurrencyTemplate;
import org.springframework.util.StringUtils;

/**
 * @Author Lijl
 * @ClassName FiveDpiServiceImpl
 * @Description 5G dpi
 * @Date 2021/11/29 9:38
 * @Version 1.0
 */
@ActionService(value = "5gdpi")
public class FiveDpiServiceImpl extends AbstractCurrencyTemplate implements BaseService<Object, RestBodyEntity> {

    private final QueryTaskManager queryTaskManager;
    private final RouterConfig routerConfig;

    public FiveDpiServiceImpl(QueryTaskManager queryTaskManager, RouterConfig routerConfig) {
        this.queryTaskManager = queryTaskManager;
        this.routerConfig = routerConfig;
    }

    @Override
    public Object actionMethod(RestBodyEntity restBodyEntity) {
        String msisdn = restBodyEntity.getMsisdn();
        String imsi = restBodyEntity.getImsi();
        String imei = restBodyEntity.getImei();
        if (StringUtils.hasLength(msisdn)){
        return super.execute(restBodyEntity,
                (province, tableNameList, startAndEndRowKeys) ->
                super.mappingField(queryTaskManager.query(province,tableNameList,startAndEndRowKeys)),"5GDPI","xdr", routerConfig.getFiveDpi());
        }else if (StringUtils.hasLength(imsi)||StringUtils.hasLength(imei)){
            return super.executeIndex(restBodyEntity,(family, qualifier, startVal, endVal,qualifierAndVal, rowVal, tableNames)
                            -> super.mappingField(queryTaskManager.queryIndex(family,qualifier,startVal,endVal,qualifierAndVal,rowVal,tableNames)),
                    "5GDPI","xdr", routerConfig.getFiveDpi());
        }
        throw new QueryException(ResultCode.VALIDATE_FAILED);
    }
}
