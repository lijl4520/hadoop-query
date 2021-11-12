/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.service;

import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.exception.Asserts;
import com.huawei.commons.Actuator;
import com.huawei.commons.encryption.EncryAndDecryService;
import com.huawei.commons.QueryDataCallback;
import com.huawei.querys.domain.rest.RestBodyEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Lijl
 * @ClassName AbstractService
 * @Description 业务基类
 * @Date 2021/10/14 16:49
 * @Version 1.0
 */
public abstract class AbstractService implements Actuator<RestBodyEntity> {

    private Router router;

    @Autowired
    public void setRouter(Router router) {
        this.router = router;
    }

    private EncryAndDecryService encryAndDecryService;

    @Autowired
    public void setEncryAndDecryService(EncryAndDecryService encryAndDecryService) {
        this.encryAndDecryService = encryAndDecryService;
    }

    /**
     * @Author lijiale
     * @MethodName execute
     * @Description 获取表名、rowkey,执行查询数据回调
     * @Date 17:38 2021/11/10
     * @Version 1.0
     * @param restBodyEntity
     * @param callback
     * @return: T
    **/
    @Override
    public <T> T execute(RestBodyEntity restBodyEntity, QueryDataCallback<T> callback){
        List<String> tableNameList = this.getTableNameList(restBodyEntity,"GN");
        List<String> startAndEndRowKeys = this.getStartAndEndRowKeys(restBodyEntity);
        return callback.doInData(restBodyEntity.getProvince(),tableNameList,startAndEndRowKeys);
    }

    /**
     * @Author lijiale
     * @MethodName getTableNameList
     * @Description 获取表名
     * @Date 17:12 2021/10/14
     * @Version 1.0
     * @param restBodyEntity
     * @param prefix
     * @return: java.util.List<java.lang.String>
    **/
    private List<String> getTableNameList(RestBodyEntity restBodyEntity, final String prefix){
        List<String> tableNames = router.getTableNames(restBodyEntity);
        return tableNames.stream().map(suffix ->
                new StringBuilder("DWA_HW:")
                        .append(prefix)
                        .append(suffix)
                        .toString())
                .collect(Collectors.toList());
    }

    /**
     * @Author lijiale
     * @MethodName getStartAndEndRowKeys
     * @Description 生成rowkey
     * @Date 17:01 2021/10/15
     * @Version 1.0
     * @param restBody
     * @return: java.util.List<java.lang.String>
    **/
    private List<String> getStartAndEndRowKeys(RestBodyEntity restBody) {
        StringBuffer startRowKeySb = new StringBuffer();
        StringBuffer endRowKeySb = new StringBuffer();
        String cellNum = "";
        if (restBody.getMsisdn()!=null){
            cellNum = restBody.getMsisdn();
        }else if (restBody.getImsi()!=null){
            cellNum = restBody.getImsi();
        }else if(restBody.getImei()!=null){
            cellNum = restBody.getImei();
        }
        if ("".equals(cellNum)){
            Asserts.fail(ResultCode.VALIDATE_FAILED);
        }
        String cipherText = encryAndDecryService.sm4Ecnew(cellNum);
        if (cipherText==null||"".equals(cipherText)){
            Asserts.fail(ResultCode.ENCRYP_DECRYP);
        }

        List<String> rowKeyList = new LinkedList<String>();
        startRowKeySb.append(cipherText).append("_")
                .append(restBody.getStartTime().atZone(ZoneId.of("Asia/Shanghai"))
                        .toInstant()
                        .toEpochMilli());
        endRowKeySb.append(cipherText).append("_")
                .append(restBody.getEndTime().atZone(ZoneId.of("Asia/Shanghai"))
                        .toInstant()
                        .toEpochMilli());
        rowKeyList.add(startRowKeySb.toString());
        rowKeyList.add(endRowKeySb.toString());
        return rowKeyList;
    }
}
