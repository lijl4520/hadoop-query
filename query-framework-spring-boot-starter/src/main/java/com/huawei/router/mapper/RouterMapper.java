/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.router.mapper;

import com.huawei.router.*;

/**
 * @Author Lijl
 * @InterfaceName ImsRouterMapper
 * @Description ims
 * @Date 2022/3/7 10:37
 * @Version 1.0
 */
public interface RouterMapper {

    /**
     * @Author lijiale
     * @MethodName findMmeRouterConfig
     * @Description 4g位置路由查询
     * @Date 11:00 2022/3/7
     * @Version 1.0
     * @param
     * @return: com.huawei.router.MmeRouterConfig
    **/
    MmeRouterConfig findMmeRouterConfig();

    /**
     * @Author lijiale
     * @MethodName findImsRouterConfig
     * @Description Ims 路由查询
     * @Date 11:00 2022/3/7
     * @Version 1.0
     * @param 
     * @return: com.huawei.router.ImsRouterConfig
    **/
    ImsRouterConfig findImsRouterConfig();

    /**
     * @Author lijiale
     * @MethodName findDpiRouterConfig
     * @Description 4gDpi
     * @Date 11:01 2022/3/7
     * @Version 1.0
     * @param 
     * @return: com.huawei.router.DpiRouterConfig
    **/
    DpiRouterConfig findDpiRouterConfig();

    /**
     * @Author lijiale
     * @MethodName findFiveDpiRouterConfig
     * @Description 5gDpi
     * @Date 11:01 2022/3/7
     * @Version 1.0
     * @param
     * @return: com.huawei.router.FiveDpiRouterConfig
    **/
    FiveDpiRouterConfig findFiveDpiRouterConfig();

    /**
     * @Author lijiale
     * @MethodName findFiveNxRouterConfig
     * @Description 5G位置
     * @Date 11:02 2022/3/7
     * @Version 1.0
     * @param
     * @return: com.huawei.router.FiveNxRouterConfig
    **/
    FiveNxRouterConfig findFiveNxRouterConfig();

    /**
     * @Author lijiale
     * @MethodName findGnRouterConfig
     * @Description 23G位置
     * @Date 11:02 2022/3/7
     * @Version 1.0
     * @param 
     * @return: com.huawei.router.GnRouterConfig
    **/
    GnRouterConfig findGnRouterConfig();
}
