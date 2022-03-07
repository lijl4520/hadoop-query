/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.impl;

import com.huawei.commons.domain.HbaseInstance;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @Author Lijl
 * @ClassName ColumnIndexQueryTask
 * @Description 二级索引查询任务
 * @Date 2021/12/24 13:34
 * @Version 1.0
 */
public class ColumnIndexQueryTask implements Callable {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 列簇
     */
    private String family;
    /**
     * 区间查询列
     */
    private String qualifier;
    /**
     * 区间查询开始值
     */
    private Long startVal;
    /**
     * 区间查询结束值
     */
    private Long endVal;
    /**
     * 二级索引查询列-值
     */
    private Map<String,String> qualifierAndVal;
    /**
     * rowkey过滤值
     */
    private String rowVal;

    private HbaseInstance hbase;

    private RowMapper rowMapper;


    public ColumnIndexQueryTask(HbaseInstance hbase, String tableName, String family, String qualifier,
                                Long startVal, Long endVal,
                                Map<String, String> qualifierAndVal, String rowVal, RowMapper rowMapper) {
        this.hbase = hbase;
        this.tableName = tableName;
        this.family = family;
        this.qualifier = qualifier;
        this.startVal = startVal;
        this.endVal = endVal;
        this.qualifierAndVal = qualifierAndVal;
        this.rowVal = rowVal;
        this.rowMapper = rowMapper;
    }

    @Override
    public Object call() {
        List list = hbase.getHbaseOperations().find(this.tableName, this.family, this.qualifier, this.startVal,
                this.endVal, this.qualifierAndVal, this.rowVal, this.rowMapper);
        hbase.setStatus(0);
        return list;
    }
}
