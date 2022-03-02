/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.crosscluster.service;

import com.huawei.commons.Actuator;
import com.huawei.commons.QueryDataCallback;
import com.huawei.commons.QueryIndexDataCallback;
import com.huawei.commons.domain.AbstractRouterConfig;
import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.exception.Asserts;
import com.huawei.commons.exception.QueryException;
import com.huawei.crosscluster.domain.rest.RestBodyEntity;
import com.huawei.ende.encryption.EncryAndDecryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Lijl
 * @ClassName AbstractCurrencyTemplate
 * @Description 通过模板
 * @Date 2021/11/19 16:48
 * @Version 1.0
 */
@Slf4j
public abstract class AbstractCurrencyTemplate implements Actuator<RestBodyEntity> {


    private TableNameRouter tableNameRouter;

    @Autowired
    public void setTableNameRouter(TableNameRouter tableNameRouter) {
        this.tableNameRouter = tableNameRouter;
    }

    private EncryAndDecryService encryAndDecryService;

    @Autowired
    public void setEncryAndDecryService(EncryAndDecryService encryAndDecryService) {
        this.encryAndDecryService = encryAndDecryService;
    }

    @Override
    public <T> T execute(RestBodyEntity restBodyEntity, QueryDataCallback<T> callback, String prefix,
                         String databaseName, AbstractRouterConfig routerConfig) {
        List<String> tableNameList = this.getTableNameList(restBodyEntity, prefix, databaseName,routerConfig);
        List<String> startAndEndRowKeys = this.getStartAndEndRowKeys(restBodyEntity);
        return callback.doInData(restBodyEntity.getProvince(),tableNameList,startAndEndRowKeys);
    }

    @Override
    public <T> T executeIndex(RestBodyEntity restBodyEntity, QueryIndexDataCallback<T> callback, String prefix,
                              String databaseName, AbstractRouterConfig routerConfig){
        String imei = restBodyEntity.getImei();
        String imsi = restBodyEntity.getImsi();
        List<String> tableNameList = this.getTableNameList(restBodyEntity, prefix, databaseName,routerConfig);
        Map<String,String> keyVal = new HashMap<>(2);
            if (StringUtils.hasLength(imsi)){
                keyVal.put("c4",imsi);
            }
            if (StringUtils.hasLength(imei)){
                keyVal.put("c5", imei);
            }
        return callback.doInData("cf","c1",restBodyEntity.getStartTime().atZone(ZoneId.of("Asia/Shanghai"))
                .toInstant()
                .toEpochMilli(),restBodyEntity.getEndTime().atZone(ZoneId.of("Asia/Shanghai"))
                .toInstant()
                .toEpochMilli(),keyVal,restBodyEntity.getProvince(),tableNameList.stream().toArray(String[]::new));
    }


    /**
     * @Author lijiale
     * @MethodName getTableNameList
     * @Description 获取表名
     * @Date 17:12 2021/10/14
     * @Version 1.0
     * @param restBodyEntity
     * @param prefix
     * @param databaseName
     * @return: java.util.List<java.lang.String>
     **/
    private List<String> getTableNameList(RestBodyEntity restBodyEntity, final String prefix, final String databaseName,
                                          final AbstractRouterConfig routerConfig){
        List<String> tableNames = tableNameRouter.getTableNames(restBodyEntity,routerConfig.getEffectiveTime(), routerConfig.getOldTimeInterval(), routerConfig.getNewerTimeInterval());
        return tableNames.stream().map(suffix ->
                new StringBuilder(databaseName)
                        .append(":")
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
        String cipherText = null;
        try {
            cipherText = encryAndDecryService.sm4Ecnew(cellNum);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            Asserts.fail(ResultCode.ENCRYP_DECRYP);
        }
        if (cipherText==null||"".equals(cipherText)){
            Asserts.fail(ResultCode.ENCRYP_DECRYP);
        }

        List<String> rowKeyList = new LinkedList<String>();
        String prefix = encryAndDecryService.createPrefix(cipherText);
        startRowKeySb.append(prefix).append("_")
                .append(cipherText).append("_")
                .append(restBody.getStartTime().atZone(ZoneId.of("Asia/Shanghai"))
                        .toInstant()
                        .toEpochMilli());
        endRowKeySb.append(prefix).append("_")
                .append(cipherText).append("_")
                .append(restBody.getEndTime().atZone(ZoneId.of("Asia/Shanghai"))
                        .toInstant()
                        .toEpochMilli());
        rowKeyList.add(startRowKeySb.toString());
        rowKeyList.add(endRowKeySb.toString());
        return rowKeyList;
    }

    /**
     * @Author lijiale
     * @MethodName mappingField
     * @Description 映射数据字段
     * @Date 13:45 2021/12/1
     * @Version 1.0
     * @param resultList
     * @return: java.util.List
    **/
    protected List mappingField(List<Map<String,Object>> resultList){
        List list = new ArrayList();
        if (resultList!=null) {
            resultList.forEach(map -> {
                Map<String, Object> fm = new HashMap<>(5);
                map.forEach((k, v) -> {
                    if ("c2".equals(k)) {
                        fm.put("version", v);
                    }
                    if ("c3".equals(k)) {
                        fm.put("protocol", v);
                    }
                    if ("c4".equals(k)) {
                        fm.put("IMSI", v);
                    }
                    if ("c5".equals(k)) {
                        fm.put("IMEI", v);
                    }
                    if ("c6".equals(k)) {
                        fm.put("dataContent", v);
                    }
                });
                list.add(fm);
            });
        }
        return list;
    }
}
