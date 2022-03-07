/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */
package com.huawei.commons.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @Author Lijl
 * @AnnotationTypeName ActionService
 * @Description 服务注解
 * @Date 2021/10/19 15:07
 * @Version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface ActionService {
    String value();
}
