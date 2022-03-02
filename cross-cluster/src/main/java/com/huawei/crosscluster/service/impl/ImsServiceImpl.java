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
 * @ClassName ImsServiceImpl
 * @Description ims
 * @Date 2021/11/19 16:44
 * @Version 1.0
 */
@ActionService(value = "ims")
public class ImsServiceImpl extends AbstractCurrencyTemplate implements BaseService<Object, RestBodyEntity> {

    private final QueryTaskManager queryTaskManager;
    private final RouterConfig routerConfig;

    public ImsServiceImpl(QueryTaskManager queryTaskManager,RouterConfig routerConfig) {
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
                    (province, tableNameList, startAndEndRowKeys)->
                            super.mappingField(queryTaskManager.query(province,tableNameList,startAndEndRowKeys)),
                    "IMS","xdr",routerConfig.getIms());
        }else if (StringUtils.hasLength(imei)||StringUtils.hasLength(imsi)){
            return super.executeIndex(restBodyEntity,(family, qualifier, startVal, endVal,qualifierAndVal, rowVal, tableNames)
                    -> super.mappingField(queryTaskManager.queryIndex(family,qualifier,startVal,endVal,qualifierAndVal,rowVal,tableNames)),
                    "IMS","xdr",routerConfig.getIms());
        }
        throw new QueryException(ResultCode.VALIDATE_FAILED);
    }
}
