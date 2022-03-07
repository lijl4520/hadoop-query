/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.crosscluster.service;

import com.huawei.commons.exception.Asserts;
import com.huawei.crosscluster.domain.rest.RestBodyEntity;
import com.huawei.router.AbstractRouter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Lijl
 * @ClassName TableNameRouter
 * @Description 表名路由
 * @Date 2021/11/19 16:40
 * @Version 1.0
 */
@Component
public class TableNameRouter {

    /**
     * @Author lijl
     * @MethodName getTableNames
     * @Description 根据开始、结束时间获取所需Hbase表名时间后缀
     * @Date 15:29 2021/9/7
     * @Version 1.0
     * @param restBodyEntity 参数实体
     * @return: java.util.List<java.lang.String>
     **/
    public List<String> getTableNames(RestBodyEntity restBodyEntity, String effectiveTime,
                                      List<? extends AbstractRouter> oldTimeInterval, List<? extends AbstractRouter> newerTimeInterval){
        return isEffectiveTime(oldTimeInterval,newerTimeInterval,effectiveTime,restBodyEntity);
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
     * @param restBodyEntity 参数实体
     * @return: java.util.List<java.lang.String>
     **/
    private List<String> isEffectiveTime(List<? extends AbstractRouter> oldTimeInterval, List<? extends AbstractRouter> newerTimeInterval, String effectiveTime, RestBodyEntity restBodyEntity){
        List<String> tableNames = new LinkedList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime stDate = restBodyEntity.getStartTime();
        LocalDateTime edDate = restBodyEntity.getEndTime();
        if (!(edDate.isEqual(stDate) || edDate.isAfter(stDate))){
            Asserts.fail("开始时间不得大于结束时间");
        }
        LocalDateTime ettDate = null;
        boolean bol = false;
        if (effectiveTime!=null){
            ettDate = LocalDateTime.parse(effectiveTime + "000000", dtf);
            bol = true;
        }
        if (bol && stDate.isAfter(ettDate) && stDate.isEqual(ettDate)){
            getTableNames(stDate,edDate,newerTimeInterval,tableNames);
            careteExpandTable(tableNames,newerTimeInterval,false,null,null,null);
        }else if (bol && edDate.isAfter(ettDate) && edDate.isEqual(ettDate)){
            getTableNames(stDate,ettDate.minusMinutes(1),oldTimeInterval,tableNames);
            getTableNames(ettDate,edDate,newerTimeInterval,tableNames);
            careteExpandTable(tableNames,newerTimeInterval,false,null,null,null);
        }else{
            getTableNames(stDate,edDate,oldTimeInterval,tableNames);
            careteExpandTable(tableNames,oldTimeInterval,false,null,newerTimeInterval,effectiveTime);
        }
        return tableNames;
    }

    /**
     * @Author lijiale
     * @MethodName careteExpandTable
     * @Description 生成拓展表名
     * @Date 17:23 2021/10/21
     * @Version 1.0
     * @param tableNames
     * @param timeInterval
     * @param b
     * @param fDate
     * @param newTimeInterval
     * @param effectiveTime
     * @return: void
     **/
    private void careteExpandTable(List<String> tableNames, List<? extends AbstractRouter> timeInterval,
                                   boolean b,String fDate,List<? extends AbstractRouter> newTimeInterval,
                                   String effectiveTime) {
        //判断是否已按规则生成了表
        if (tableNames.size()>0){
            String tableName = tableNames.get(tableNames.size()-1);
            String[] timeArr = tableName.split("_");
            //是否传入时间
            if (fDate==null){
                fDate = timeArr[1];
            }
            String hours = timeArr[2];
            int hours1 = Integer.parseInt(hours.substring(0, 2));
            int hours2 = Integer.parseInt(hours.substring(2, 4));
            int size = timeInterval.size();
            for (int i = 0; i < size; i++) {
                AbstractRouter router = timeInterval.get(i);
                int startDate = router.getStartDate();
                int endDate = router.getEndDate();
                if (b){
                    tableNames.add("_"+fDate+"_"
                            +(startDate<=9?"0"+startDate:startDate)+(endDate<=9?"0" + endDate:endDate));
                    break;
                }
                if (hours1==startDate && hours2==endDate){
                    if ((i+1)==size){
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                        LocalDate localDate = LocalDate.parse(fDate, dtf).plusDays(1);
                        String newDate = dtf.format(localDate);
                        if (effectiveTime!=null && effectiveTime.equals(newDate)){
                            careteExpandTable(tableNames,newTimeInterval,
                                    true,newDate,null,null);
                        }else{
                            careteExpandTable(tableNames,timeInterval,
                                    true,newDate,null,null);
                        }
                        break;
                    }
                    b = true;
                }
            }
        }
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
    public List<String> getTableNames(LocalDateTime stDate, LocalDateTime edDate, List<? extends AbstractRouter> timeIntervalList, List<String> tableNames){
        long between = computeDay(stDate,edDate);
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
     * @Author lijiale
     * @MethodName computeDay
     * @Description 计算跨多少天
     * @Date 15:42 2021/10/28
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
     * @param edDateFl 同日结束时间
     * @param tableNames 记录表名集合
     * @param timeIntervalList 建表时间段
     * @return: void
     **/
    private void calculateDateStr(LocalDateTime stDateFl,LocalDateTime edDateFl,List<String> tableNames,List<? extends AbstractRouter> timeIntervalList){
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
