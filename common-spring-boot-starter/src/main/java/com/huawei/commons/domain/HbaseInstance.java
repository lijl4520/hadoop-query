/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.domain;

import com.huawei.commons.impl.HbaseOperations;
import lombok.Data;

import java.util.Date;

/**
 * @Author Lijl
 * @ClassName HbaseInstance
 * @Description TODO
 * @Date 2021/11/9 16:48
 * @Version 1.0
 */
@Data
public class HbaseInstance {
    /**
     * 创建时间
     */
    private Date data;
    /**
     * hbase操作实例
     */
    private HbaseOperations hbaseOperations;
    /**
     * 使用状态
     */
    private int status;
    /**
     * 是否过期
     */
    private boolean timeout;
}
