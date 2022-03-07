/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.domian;

import com.huawei.router.service.LoadDataBaseRouter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Author Lijl
 * @ClassName DefaultRouterProcessor
 * @Description TODO
 * @Date 2022/3/4 16:24
 * @Version 1.0
 */
@Component
public class DefaultRouterProcessor implements ApplicationListener<ContextRefreshedEvent> {

    private LoadDataBaseRouter loadDataBaseRouter;

    public DefaultRouterProcessor(LoadDataBaseRouter loadDataBaseRouter){
        this.loadDataBaseRouter = loadDataBaseRouter;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        if (Objects.isNull(applicationContext.getParent())){
            //for (int i = 1; i <= 6; i++) {
                loadDataBaseRouter.loadRouter(1);
            //}
        }
    }
}
