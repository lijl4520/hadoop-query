/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.domian.manager;

import com.alibaba.fastjson.JSONObject;
import com.huawei.commons.BaseService;
import com.huawei.commons.annotation.ActionService;
import com.huawei.commons.exception.Asserts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Lijl
 * @ClassName BaseServiceManager
 * @Description 服务基类管理
 * @Date 2021/10/19 15:22
 * @Version 1.0
 */
@Slf4j
@Component
public class BaseServiceManager {

    private static Map<String, BaseService> baseServiceMap;

    public static Object data(Object data){
        return data;
    }

    public BaseServiceManager(List<BaseService> baseServices){
        baseServiceMap = baseServices.stream().collect(Collectors.toMap(baseService ->
                AnnotationUtils.findAnnotation(baseService.getClass(), ActionService.class).value(),
                v -> v,
                (v1,v2) -> v1));
    }

    public static class Builder{
        private BaseService baseService;
        private JSONObject json;

        public Builder(){
        }

        public Builder action(String action){
            BaseService baseService = baseServiceMap.get(action);
            if (baseService==null){
                Asserts.fail("服务不存在");
            }
            this.baseService = baseService;
            return this;
        }

        public Builder object(JSONObject json){
            this.json = json;
            if (json==null){
                Asserts.fail("参数为空");
            }
            return this;
        }

        public Object build(){
            long startTime = System.currentTimeMillis();
            Class<? extends BaseService> aClass = this.baseService.getClass();
            ParameterizedType parameterizedType = (ParameterizedType) aClass.getGenericInterfaces()[0];
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Object data = BaseServiceManager.data(this.baseService.actionMethod(this.json.toJavaObject(actualTypeArguments[1])));
            long endTime = System.currentTimeMillis();
            log.info("线程->{},查询耗时:{}毫秒",Thread.currentThread().getName(),endTime-startTime);
            return data;
        }
    }
}
