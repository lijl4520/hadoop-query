/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.xdrs.service;

import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.exception.Asserts;
import com.huawei.commons.service.EncryAndDecryService;
import com.huawei.commons.service.HbaseManager;
import com.huawei.commons.service.QueryTaskManager;
import com.huawei.xdrs.domain.rest.RequestBodyEntity;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Xdrs
 *
 * @since 2021/9/2
 */
public abstract class Xdrs {

    private Router router;

    protected HbaseManager hbaseManager;

    protected QueryTaskManager queryTaskManager;

    @Autowired
    public void setRouter(Router router) {
        this.router = router;
    }
    @Autowired
    public void setHbaseManager(HbaseManager hbaseManager) {
        this.hbaseManager = hbaseManager;
    }
    @Autowired
    public void setQueryTaskManager(QueryTaskManager queryTaskManager) {
        this.queryTaskManager = queryTaskManager;
    }

    private EncryAndDecryService encryAndDecryService;

    @Autowired
    public void setEncryAndDecryService(EncryAndDecryService encryAndDecryService) {
        this.encryAndDecryService = encryAndDecryService;
    }

    /**
     * @Author Lijl
     * @MethodName getTableNameList
     * @Description 拼接表名前后缀并返回表名集合
     * @Date 17:21 2021/9/7
     * @Version 1.0
     * @param requestBodyEntity 查询条件参数
     * @param prefix HBase表名前缀
     * @return: java.util.List<java.lang.String>
    **/
    protected List<String> getTableNameList(RequestBodyEntity requestBodyEntity, String prefix){
        List<String> tableNames = router.getTableNames(requestBodyEntity);
        return tableNames.stream().map(suffix -> prefix+suffix).collect(Collectors.toList());
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
    protected List<String> getStartAndEndRowKeys(RequestBodyEntity requestBody) {
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
        startRowKeySb.append(cipherText).append("_")
                .append(requestBody.getStartTime()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .toInstant()
                        .toEpochMilli());
        endRowKeySb.append(cipherText).append("_")
                .append(requestBody.getEndTime()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .toInstant()
                        .toEpochMilli());
        String province = requestBody.getProvince();
        if (province!=null&&!"".equals(province)){
            startRowKeySb.append("_")
                    .append(province);
            endRowKeySb.append("_")
                    .append(province);
        }
        String city = requestBody.getCity();
        if (city!=null&&!"".equals(city)){
            startRowKeySb.append("_").append(city);
            endRowKeySb.append("_").append(city);
        }
        rowKeyList.add(startRowKeySb.toString());
        rowKeyList.add(endRowKeySb.toString());
        return rowKeyList;
    }

    /**
     * @Author lijiale
     * @MethodName toMapList
     * @Description 将查询出来的数据流转换成可读对象
     * @Date 10:13 2021/9/13
     * @Version 1.0
     * @param resultList
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
    **/
    protected List<Map<String,Object>> toMapList(List<Result> resultList){
        if (resultList.size()>10000){
            Asserts.fail(ResultCode.DATA_EXCESS);
        }
        List<Map<String,Object>> dataList;
        if (resultList!=null){
            dataList = new ArrayList<>();
            for (Result result : resultList) {
                Map<String,Object> resultMap = new HashMap<String,Object>(16);
                List<Cell> cells = result.listCells();
                if (cells!=null && cells.size()>0){
                    for (int i = 0; i < cells.size(); i++) {
                        Cell cell = cells.get(i);
                        resultMap.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),
                                Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength())
                        );
                    }
                    dataList.add(resultMap);
                }
            }
            return dataList;
        }
        return null;
    }
}
