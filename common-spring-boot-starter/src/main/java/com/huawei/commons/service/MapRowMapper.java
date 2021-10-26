/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.service;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Lijl
 * @ClassName MapRowMapper
 * @Description 转换实体
 * @Date 2021/10/25 15:42
 * @Version 1.0
 */
public class MapRowMapper implements RowMapper<Map>{
    @Override
    public Map mapRow(Result result) throws Exception {
        Map<String,Object> map = new HashMap<>(16);
        List<Cell> cells = result.listCells();
        if (cells!=null&&cells.size()>0){
            for (int i = 0; i < cells.size(); i++) {
                Cell cell = cells.get(i);
                map.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),
                        Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength())
                );
            }
        }
        return map;
    }
}
