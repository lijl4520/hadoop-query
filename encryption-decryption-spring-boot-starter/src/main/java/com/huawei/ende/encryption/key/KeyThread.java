/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.ende.encryption.key;

import com.huawei.ende.domain.EncrypDecrypProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KeyThread implements Runnable {
    private KeyService keyService;
    private IKeyExecutor keyExecutor;
    private String paramMonth;
    private int managerCycle = 86400000;
    private static boolean refreshFlag = true;

    public KeyThread(KeyService keyService, EncrypDecrypProperties authProperties, String paramMonth) {
        this.keyService = keyService;
        this.keyExecutor = new HttpKeyExecutor(authProperties);
        this.paramMonth = paramMonth;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(300000L);
            ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory
                            .Builder()
                            .namingPattern("scheduled-pool-%d")
                            .daemon(true)
                            .build());
            scheduledExecutorService.scheduleAtFixedRate(()->{
                KeyThread.refreshFlag = true;
                LocalDate now = LocalDate.now();
                DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd");
                this.paramMonth = now.format(ofPattern);
                LocalDate curDate = LocalDate.now();
                LocalDate upeerDate = LocalDate.parse(curDate.getYear() + "01", ofPattern);
                long between = ChronoUnit.MONTHS.between(upeerDate, curDate);
                DateTimeFormatter mDtf = DateTimeFormatter.ofPattern("yyyyMM");
                if (between!=0){
                    for (long i = 1; i <= between; i++) {
                        this.keyService.getKeyMap().remove("SM4_"+curDate.minusMonths(i).format(mDtf));
                    }
                }
                this.process();
            }, 0, this.managerCycle, TimeUnit.MILLISECONDS);
        } catch (InterruptedException var2) {
            log.error(var2.getMessage(), var2);
        }

    }

    public void process() {
        Map keyMap = this.keyService.getKeyMap();
        String[] months = this.calcMonths();
        for(int i = 0; i < months.length; ++i) {
            String curMonth = months[i];
            String mapKey = "SM4_" + curMonth;
            if (!keyMap.containsKey(mapKey) || refreshFlag) {
                try {
                    String secretKeyAndExpireTime = this.keyExecutor.getHttpKey(curMonth + "01000000");
                    String[] keySplits = secretKeyAndExpireTime.split(",");
                    if (keySplits.length > 1) {
                        this.writeMap(keySplits, curMonth, keyMap);
                        refreshFlag = false;
                    } else {
                        log.warn("get the secretKey and ExpireTime is null,Please check whether the security center has the key for this month " + curMonth);
                        this.managerCycle = 3600000;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        this.keyService.setKeyMap(keyMap);
    }

    private HashSet<String> segment(String beginTime, String endTime) {
        HashSet monthSet = new HashSet();
        Calendar beginCldr = new GregorianCalendar();
        GregorianCalendar endCldr = new GregorianCalendar();

        try {
            beginCldr.setTime(DateUtils.parseDateStrictly(beginTime, new String[]{"yyyyMM"}));
            endCldr.setTime(DateUtils.parseDateStrictly(endTime, new String[]{"yyyyMM"}));

            while(beginCldr.compareTo(endCldr) <= 0) {
                monthSet.add(DateFormatUtils.format(beginCldr, "yyyyMM"));
                beginCldr.add(2, 1);
            }
        } catch (ParseException var7) {
            log.error(var7.getMessage(), var7);
        }

        return monthSet;
    }

    private String[] calcMonths() {
        String[] months = new String[3];
        int m = 0;
        Calendar calendar = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

        try {
            String originMonth = String.valueOf(this.paramMonth);
            String formatMonth = originMonth.substring(0, 6);
            Date date = sdf.parse(formatMonth);

            for(int i = -1; i <= 1; ++i) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(2, i);
                months[m++] = DateFormatUtils.format(calendar, "yyyyMM");
            }

            return months;
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    private void writeMap(String[] keySplits, String curMonth, Map<String, String> keyMap) {
        String secretKey = keySplits[0];
        String expireTime = keySplits[1];
        if (StringUtils.isNotBlank(expireTime) && expireTime.length() >= 6 && StringUtils.isNotBlank(secretKey)) {
            this.managerCycle = 86400000;
            expireTime = expireTime.substring(0, 6);
            if (curMonth.compareTo(expireTime) <= 0) {
                Set set = this.segment(curMonth, expireTime);
                Iterator it = set.iterator();

                while(it.hasNext()) {
                    String str = (String)it.next();
                    keyMap.put("SM4_" + str, secretKey);
                }
            }
        }

    }
}
