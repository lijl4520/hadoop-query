/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.service;

import com.huawei.querys.domain.rest.RestBodyEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Lijl
 * @ClassName Router
 * @Description 路由
 * @Date 2021/10/15 10:14
 * @Version 1.0
 */
@Component
public class Router {

    /**
     * @Author lijl
     * @MethodName getTableNames
     * @Description 根据开始、结束时间获取所需Hbase表名时间后缀
     * @Date 15:29 2021/9/7
     * @Version 1.0
     * @return: java.util.List<java.lang.String>
     **/
    public List<String> getTableNames(RestBodyEntity restBodyEntity){
        return isEffectiveTime(restBodyEntity);
    }

    /**
     * @Author lijiale
     * @MethodName isEffectiveTime
     * @Description 判断生效时间-分割表名生成规则
     * @Date 11:10 2021/9/9
     * @Version 1.0
     * @param requestBodyEntity 参数实体
     * @return: java.util.List<java.lang.String>
     **/
    private List<String> isEffectiveTime(RestBodyEntity requestBodyEntity){
        List<String> tableNames = new LinkedList<>();
        LocalDateTime stDate = requestBodyEntity.getStartTime();
        LocalDateTime edDate = requestBodyEntity.getEndTime();
        getTableNames(stDate,edDate,tableNames);
        return tableNames;
    }

    /**
     * @Author lijl
     * @MethodName getTableNames
     * @Description 生成表名时间后缀
     * @Date 11:13 2021/9/9
     * @Version 1.0
     * @param stDate 表名开始时间段
     * @param edDate 表名结束时间段
     * @param tableNames 记录表名集合
     * @return: java.util.List<java.lang.String>
    **/
    public List<String> getTableNames(LocalDateTime stDate, LocalDateTime edDate, List<String> tableNames){
        long between = computeDay(stDate,edDate);;
        for (long i = 0; i <= between+1L; i++) {
            LocalDateTime localDateTime = stDate;
            if (i>0){
                localDateTime = stDate.plusDays(i);
            }
            calculateDateStr(localDateTime,tableNames);
        }
        return tableNames;
    }

    /**
     * @Author lijiale
     * @MethodName computeDay
     * @Description 计算相差天数
     * @Date 17:11 2021/10/28
     * @Version 1.0
     * @param stDate
     * @param edDate
     * @return: long
    **/
    private long computeDay(LocalDateTime stDate, LocalDateTime edDate) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        String stStr = dtf.format(stDate);
        String edStr = dtf.format(edDate);
        LocalDate staDate = LocalDate.parse(stStr, dtf);
        LocalDate endDate = LocalDate.parse(edStr, dtf);
        long between = ChronoUnit.DAYS.between(staDate, endDate);
        return between;
    }

    /**
     * @Author lijl
     * @MethodName calculateDateStr
     * @Description 抽取表名时间后缀
     * @Date 15:32 2021/9/7
     * @Version 1.0
     * @param stDateFl 同日开始时间
     * @param tableNames 记录表名集合
     * @return: void
    **/
    private void calculateDateStr(LocalDateTime stDateFl,List<String> tableNames){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String str = "_"+formatter.format(stDateFl)+"_0024";
        tableNames.add(str);
    }
}
