/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.crosscluster.service.impl;

import com.huawei.commons.BaseService;
import com.huawei.commons.annotation.ActionService;
import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.exception.QueryException;
import com.huawei.commons.manager.QueryTaskManager;
import com.huawei.crosscluster.domain.rest.RestBodyEntity;
import com.huawei.crosscluster.service.AbstractCurrencyTemplate;
import com.huawei.ende.domain.region.RegionNumConfig;
import com.huawei.router.RouterConfig;
import org.springframework.util.StringUtils;

/**
 * @Author Lijl
 * @ClassName DpiServiceImpl
 * @Description 4G dpi
 * @Date 2021/11/29 9:33
 * @Version 1.0
 */
@ActionService(value = "dpi")
public class DpiServiceImpl extends AbstractCurrencyTemplate implements BaseService<Object, RestBodyEntity> {
    private final QueryTaskManager queryTaskManager;
    private final RouterConfig routerConfig;
    private final RegionNumConfig regionNumConfig;

    public DpiServiceImpl(QueryTaskManager queryTaskManager, RouterConfig routerConfig, RegionNumConfig regionNumConfig) {
        this.queryTaskManager = queryTaskManager;
        this.routerConfig = routerConfig;
        this.regionNumConfig = regionNumConfig;
    }


    @Override
    public Object actionMethod(RestBodyEntity restBodyEntity) {
        String msisdn = restBodyEntity.getMsisdn();
        String imsi = restBodyEntity.getImsi();
        String imei = restBodyEntity.getImei();
        if (StringUtils.hasLength(msisdn)){
            return super.execute(restBodyEntity,
                    (province, tableNameList, startAndEndRowKeys) ->super.mappingField(queryTaskManager.query(province, tableNameList, startAndEndRowKeys))
                    ,"DPI","xdr",routerConfig.getDpi(),this.regionNumConfig.getDpiNum().getRegionNum());
        }else if (StringUtils.hasLength(imsi)||StringUtils.hasLength(imei)){
            return super.executeIndex(restBodyEntity,
            (family, qualifier, startVal, endVal,qualifierAndVal, rowVal, tableNames)->super.mappingField(queryTaskManager.queryIndex(family,qualifier,startVal,endVal,qualifierAndVal,rowVal,tableNames)),
                    "DPI","xdr",routerConfig.getDpi());
        }
        throw new QueryException(ResultCode.VALIDATE_FAILED);
    }
}
