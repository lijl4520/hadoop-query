/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.service.impl;

import com.huawei.commons.domain.annotation.ActionService;
import com.huawei.commons.service.*;
import com.huawei.querys.domain.rest.RestBodyEntity;
import com.huawei.querys.service.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

    private HbaseManager hbaseManager;

    @Autowired
    public void setHbaseManager(HbaseManager hbaseManager) {
        this.hbaseManager = hbaseManager;
    }

    private QueryTaskManager queryTaskManager;

    @Autowired
    public void setQueryTaskManager(QueryTaskManager queryTaskManager) {
        this.queryTaskManager = queryTaskManager;
    }

    @Override
    public Object actionMethod(RestBodyEntity restBodyEntity) {
        List<String> tableNameList = super.getTableNameList(restBodyEntity,"GN");
        List<String> startAndEndRowKeys = super.getStartAndEndRowKeys(restBodyEntity);
        HbaseOperations hbase = this.hbaseManager.getHbaseInstance();
        List<Map<String,Object>> resultList = this.queryTaskManager.query(hbase,null, tableNameList, startAndEndRowKeys);
        if (resultList!=null){
            List list = new ArrayList();
            resultList.forEach(map -> {
                Map<String,Object> fm = new HashMap<>(5);
                map.forEach((k,v) ->{
                    if ("c2".equals(k)){
                        fm.put("version",v);
                    }
                    if ("c3".equals(k)){
                        fm.put("prorocol",v);
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
            return list;
        }
        return resultList;
    }
}
