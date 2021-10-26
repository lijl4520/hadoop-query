/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.manager;

import com.alibaba.fastjson.JSONObject;
import com.huawei.commons.domain.annotation.ActionService;
import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.domain.rest.BaseEntity;
import com.huawei.commons.exception.Asserts;
import com.huawei.commons.service.BaseService;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
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
@Component
public class BaseServiceManager{

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
                Asserts.fail(ResultCode.FAILED);
            }
            this.baseService = baseService;
            return this;
        }

        public Builder json(JSONObject json){
            this.json = json;
            return this;
        }

        public Object build(){
            Method method = this.baseService.getClass().getMethods()[0];
            Class<?> parameterType = method.getParameterTypes()[0];
            return BaseServiceManager.data(this.baseService.actionMethod(this.json.toJavaObject((Type) parameterType)));
        }
    }
}
