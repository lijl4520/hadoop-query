/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.service.impl;

import com.huawei.commons.BaseService;
import com.huawei.commons.domain.annotation.ActionService;
import com.huawei.commons.manager.QueryTaskManager;
import com.huawei.querys.domain.rest.RestBodyEntity;
import com.huawei.querys.service.*;
import org.springframework.beans.factory.annotation.Autowired;

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
@ActionService(value = "queryTwoThreeG")
public class TwoThreeGQueryServiceImpl extends AbstractService implements BaseService<Object,RestBodyEntity> {

    private QueryTaskManager queryTaskManager;

    @Autowired
    public void setQueryTaskManager(QueryTaskManager queryTaskManager) {
        this.queryTaskManager = queryTaskManager;
    }

    @Override
    public Object actionMethod(RestBodyEntity restBodyEntity) {
        return super.execute(restBodyEntity,(province,tableNameList, startAndEndRowKeys) -> {
            List<Map<String,Object>> resultList = this.queryTaskManager.query(province, tableNameList, startAndEndRowKeys);
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
        });
        /*List<String> tableNameList = super.getTableNameList(restBodyEntity,"GN");
        List<String> startAndEndRowKeys = super.getStartAndEndRowKeys(restBodyEntity);
        List<Map<String,Object>> resultList = this.queryTaskManager.query(restBodyEntity.getProvince(), tableNameList, startAndEndRowKeys);
        List list = new ArrayList();
        if (resultList!=null){
            resultList.forEach(map -> {
                Map<String,Object> fm = new HashMap<>(5);
                map.forEach((k,v) ->{
                    if ("c2".equals(k)){
                        fm.put("version",v);
                    }
                    if ("c3".equals(k)){
                        fm.put("protocol",v);
                    }
                    if ("c4".equals(k)){
                        fm.put("IMSI",v);
                    }
                    if ("c5".equals(k)){
                        fm.put("IMEI",v);
                    }
                    if ("c6".equals(k)){
                        fm.put("dataContent",v);
                    }
                });
                list.add(fm);
            });
        }
        return list;*/
    }
}
