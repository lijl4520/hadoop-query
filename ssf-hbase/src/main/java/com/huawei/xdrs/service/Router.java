/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.xdrs.service;

import com.huawei.xdrs.domain.BaseProperties;
import com.huawei.xdrs.domain.NewerTimeIntervalProperties;
import com.huawei.xdrs.domain.OldTimeIntervalProperties;
import com.huawei.xdrs.domain.RouterProperties;
import com.huawei.xdrs.domain.rest.RequestBodyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

/**
 * 路由模块
 *
 * @since 2021/9/3
 */
@Component
public class Router {

    private RouterProperties routerProperties;

    @Autowired
    public void setRouterProperties(RouterProperties routerProperties) {
        this.routerProperties = routerProperties;
    }

    /**
     * @Author lijl
     * @MethodName getTableNames
     * @Description 根据开始、结束时间获取所需Hbase表名时间后缀
     * @Date 15:29 2021/9/7
     * @Version 1.0
     * @param requestBodyEntity 参数实体
     * @return: java.util.List<java.lang.String>
     **/
    public List<String> getTableNames(RequestBodyEntity requestBodyEntity){
        List<OldTimeIntervalProperties> oldTimeInterval = routerProperties.getOldTimeInterval();
        List<NewerTimeIntervalProperties> newerTimeInterval = routerProperties.getNewerTimeInterval();
        String effectiveTime = routerProperties.getEffectiveTime();
        return isEffectiveTime(oldTimeInterval,newerTimeInterval,effectiveTime,requestBodyEntity);
    }

    /**
     * @Author lijiale
     * @MethodName isEffectiveTime
     * @Description 判断生效时间-分割表名生成规则
     * @Date 11:10 2021/9/9
     * @Version 1.0
     * @param oldTimeInterval 旧时间段
     * @param newerTimeInterval 新时间段
     * @param effectiveTime 生效时间
     * @param requestBodyEntity 参数实体
     * @return: java.util.List<java.lang.String>
     **/
    private List<String> isEffectiveTime(List<OldTimeIntervalProperties> oldTimeInterval, List<NewerTimeIntervalProperties> newerTimeInterval, String effectiveTime, RequestBodyEntity requestBodyEntity){
        List<String> tableNames = new LinkedList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime stDate = requestBodyEntity.getStartTime();
        LocalDateTime edDate = requestBodyEntity.getEndTime();
        LocalDateTime ettDate = LocalDateTime.parse(effectiveTime + "000000", dtf);
        if (stDate.isAfter(ettDate)){
            getTableNames(stDate,edDate,newerTimeInterval,tableNames);
        }else if (edDate.isAfter(ettDate)){
            getTableNames(stDate,ettDate.minusMinutes(1),oldTimeInterval,tableNames);
            getTableNames(ettDate,edDate,newerTimeInterval,tableNames);
        }else{
            getTableNames(stDate,edDate,oldTimeInterval,tableNames);
        }
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
     * @param timeIntervalList 时间段
     * @param tableNames 记录表名集合
     * @return: java.util.List<java.lang.String>
    **/
    public List<String> getTableNames(LocalDateTime stDate, LocalDateTime edDate, List<? extends BaseProperties> timeIntervalList, List<String> tableNames){
        long between = ChronoUnit.DAYS.between(stDate, edDate);
        if (between==0){
            calculateDateStr(stDate,edDate,tableNames,timeIntervalList);
        }else{
            for (long i = 0; i <= between; i++) {
                LocalDateTime stDateFl;
                LocalDateTime edDateFl;
                if (i==0){
                    stDateFl = stDate;
                    edDateFl = stDateFl.plusHours(23-stDateFl.getHour());
                }else if(i==between){
                    edDateFl = edDate;
                    stDateFl = edDateFl.minusHours(edDateFl.getHour());
                }else{
                    LocalDateTime localDateTime = stDate.plusDays(i);
                    stDateFl = localDateTime.minusHours(stDate.getHour());
                    edDateFl = localDateTime.plusHours(23-localDateTime.getHour());
                }
                calculateDateStr(stDateFl,edDateFl,tableNames,timeIntervalList);
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
     * @param edDateFl 同日结束时间
     * @param tableNames 记录表名集合
     * @param timeIntervalList 建表时间段
     * @return: void
    **/
    private void calculateDateStr(LocalDateTime stDateFl,LocalDateTime edDateFl,List<String> tableNames,List<? extends BaseProperties> timeIntervalList){
        int stHour = stDateFl.getHour();
        int year = edDateFl.getYear();
        int month = edDateFl.getMonth().getValue();
        int day = edDateFl.getDayOfMonth();

        int endHour = edDateFl.getHour();
        timeIntervalList.forEach(timeInterval -> {
            int start = timeInterval.getStartDate();
            int end = timeInterval.getEndDate();
            String str = "_"+year+(month<=9?"0"+month:month)+(day<=9?"0"+day:day)+"_";
            str = str+(start<=9?"0"+start:start)+(end<=9?"0" + end:end);
            if (end>stHour){
                tableNames.add(str);
            }
            if (endHour<start){
                tableNames.remove(str);
            }
        });
    }
}
