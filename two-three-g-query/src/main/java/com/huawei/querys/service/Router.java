/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.querys.service;

import com.huawei.querys.domain.rest.RestBodyEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
        long between = ChronoUnit.DAYS.between(stDate, edDate);
        if (between==0){
            calculateDateStr(stDate,tableNames);
        }else{
            for (long i = 0; i <= between; i++) {
                LocalDateTime localDateTime = stDate.plusDays(i+1);
                calculateDateStr(localDateTime,tableNames);
            }
        }
        return tableNames;
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
        int year = stDateFl.getYear();
        int month = stDateFl.getMonth().getValue();
        int day = stDateFl.getDayOfMonth();
        String str = "_"+year+(month<=9?"0"+month:month)+(day<=9?"0"+day:day)+"_0024";
        tableNames.add(str);
    }
}
