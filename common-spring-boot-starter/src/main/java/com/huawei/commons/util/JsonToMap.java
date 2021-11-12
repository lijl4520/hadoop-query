/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Author Lijl
 * @ClassName JsonToMap
 * @Description TODO
 * @Date 2021/11/11 17:11
 * @Version 1.0
 */
public class JsonToMap {

    public static SortedMap sortParams(JSONObject json) {
        SortedMap map = new TreeMap();
        Iterator<String> iterator = json.keySet().iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            Object o = json.get(next);
            if (o!=null&&!"".equals(o)){
                map.put(next,o);
            }
        }
        return map;
    }
}
