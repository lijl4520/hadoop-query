/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.xdrs.service;

import com.huawei.commons.Actuator;
import com.huawei.commons.domain.AbstractRouterConfig;
import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.exception.Asserts;
import com.huawei.commons.QueryDataCallback;
import com.huawei.ende.encryption.EncryAndDecryService;
import com.huawei.xdrs.domain.rest.RequestBodyEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Xdrs
 *
 * @since 2021/9/2
 */
public abstract class Xdrs implements Actuator<RequestBodyEntity> {

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

    @Override
    public <T> T execute(RequestBodyEntity requestBodyEntity, QueryDataCallback<T> callback, String prefix, String databaseName,
                         AbstractRouterConfig routerConfig) {
        List<String> tableNames = this.getTableNameList(requestBodyEntity, prefix, databaseName);
        List<String> startAndEndRowKeys = this.getStartAndEndRowKeys(requestBodyEntity);
        return callback.doInData(requestBodyEntity.getProvince(), tableNames,startAndEndRowKeys);
    }

    /**
     * @Author Lijl
     * @MethodName getTableNameList
     * @Description 拼接表名前后缀并返回表名集合
     * @Date 17:21 2021/9/7
     * @Version 1.0
     * @param requestBodyEntity 查询条件参数
     * @param prefix HBase表名前缀
     * @param databaseName
     * @return: java.util.List<java.lang.String>
    **/
    private List<String> getTableNameList(RequestBodyEntity requestBodyEntity, final String prefix, final String databaseName){
        List<String> tableNames = router.getTableNames(requestBodyEntity);
        return tableNames.stream().map(suffix ->
                new StringBuilder(databaseName)
                        .append(prefix)
                        .append(suffix)
                        .toString())
                .collect(Collectors.toList());
    }

    /**
     * @Author Lijl
     * @MethodName getStartAndEndRowKeys
     * @Description 组装start、end rowkey
     * @Date 10:48 2021/9/8
     * @Version 1.0
     * @param requestBody 入参实体
     * @return: java.util.List<java.lang.String>
    **/
    private List<String> getStartAndEndRowKeys(RequestBodyEntity requestBody) {
        StringBuffer startRowKeySb = new StringBuffer();
        StringBuffer endRowKeySb = new StringBuffer();
        String cellNum = "";
        if (requestBody.getMsisdn()!=null){
            cellNum = requestBody.getMsisdn();
        }else if (requestBody.getImsi()!=null){
            cellNum = requestBody.getImsi();
        }else if(requestBody.getImei()!=null){
            cellNum = requestBody.getImei();
        }
        if ("".equals(cellNum)){
            Asserts.fail(ResultCode.VALIDATE_FAILED);
        }
        String cipherText = encryAndDecryService.sm4Ecnew(cellNum);
        if (cipherText==null||"".equals(cipherText)){
            Asserts.fail(ResultCode.ENCRYP_DECRYP);
        }
        List<String> rowKeyList = new LinkedList<String>();
        startRowKeySb.append(encryAndDecryService.createPrefix(cipherText))
                .append("_")
                .append(cipherText).append("_")
                .append(requestBody.getStartTime()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .toInstant()
                        .toEpochMilli());
        endRowKeySb.append(cipherText).append("_")
                .append(requestBody.getEndTime()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .toInstant()
                        .toEpochMilli());
        rowKeyList.add(startRowKeySb.toString());
        rowKeyList.add(endRowKeySb.toString());
        return rowKeyList;
    }
}
