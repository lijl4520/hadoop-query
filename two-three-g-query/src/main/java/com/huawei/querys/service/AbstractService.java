/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.service;

import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.exception.Asserts;
import com.huawei.commons.service.EncryAndDecryService;
import com.huawei.querys.domain.rest.RestBodyEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Lijl
 * @ClassName AbstractService
 * @Description 业务基类
 * @Date 2021/10/14 16:49
 * @Version 1.0
 */
public abstract class AbstractService {

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
     * @MethodName getTableNameList
     * @Description 获取表名
     * @Date 17:12 2021/10/14
     * @Version 1.0
     * @param restBodyEntity
     * @param prefix
     * @return: java.util.List<java.lang.String>
    **/
    protected List<String> getTableNameList(RestBodyEntity restBodyEntity, final String prefix){
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
    protected List<String> getStartAndEndRowKeys(RestBodyEntity restBody) {
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
