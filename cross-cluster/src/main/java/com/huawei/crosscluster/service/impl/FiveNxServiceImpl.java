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
 * @ClassName FiveNxServiceImpl
 * @Description 5G 位置
 * @Date 2021/11/29 9:42
 * @Version 1.0
 */
@ActionService(value = "5gnx")
public class FiveNxServiceImpl extends AbstractCurrencyTemplate implements BaseService<Object, RestBodyEntity> {

    private final QueryTaskManager queryTaskManager;
    private final RouterConfig routerConfig;

    public FiveNxServiceImpl(QueryTaskManager queryTaskManager, RouterConfig routerConfig) {
        this.queryTaskManager = queryTaskManager;
        this.routerConfig = routerConfig;
    }


    @Override
    public Object actionMethod(RestBodyEntity restBodyEntity) {
        String msisdn = restBodyEntity.getMsisdn();
        String imei = restBodyEntity.getImei();
        String imsi = restBodyEntity.getImsi();
        if (StringUtils.hasLength(msisdn)){
            return super.execute(restBodyEntity,
                    (province, tableNameList, startAndEndRowKeys) ->
                    super.mappingField(queryTaskManager.query(province,tableNameList,startAndEndRowKeys)),"5GNX","xdr",routerConfig.getNx());
        }else if (StringUtils.hasLength(imsi)||StringUtils.hasLength(imei)){
            return super.executeIndex(restBodyEntity,(family, qualifier, startVal, endVal,qualifierAndVal, rowVal, tableNames)
                    -> super.mappingField(queryTaskManager.queryIndex(family,qualifier,startVal,endVal,qualifierAndVal,rowVal,tableNames)),
                    "5GNX","xdr",routerConfig.getNx());
        }
       throw new QueryException(ResultCode.VALIDATE_FAILED);
    }
}
