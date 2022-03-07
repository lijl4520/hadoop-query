/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.router.service;

import com.huawei.router.*;
import com.huawei.router.mapper.RouterMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Lijl
 * @ClassName DpiRouterService
 * @Description 载入数据库中的路由
 * @Date 2022/3/4 16:10
 * @Version 1.0
 */
@Service
@Slf4j
public class LoadDataBaseRouter {

    private RouterConfig routerConfig;
    private RouterMapper routerMapper;

    public LoadDataBaseRouter(RouterConfig routerConfig,
                              RouterMapper routerMapper){
        this.routerConfig = routerConfig;
        this.routerMapper = routerMapper;
    }

    public void loadRouter(int type){
        AbstractRouterConfig abstractRouterConfig = getAbstractRouterConfig(type);
        if (abstractRouterConfig==null) {
            return;
        }
        String effectiveTime = abstractRouterConfig.getEffectiveTime();
        List<? extends AbstractRouter> oldTimeInterval = abstractRouterConfig.getOldTimeInterval();
        List<? extends AbstractRouter> newerTimeInterval = abstractRouterConfig.getNewerTimeInterval();
        refreshRouter(type,effectiveTime,oldTimeInterval,newerTimeInterval);
    }

    private void refreshRouter(int type, String effectiveTime, List<? extends AbstractRouter> oldTimeInterval, List<? extends AbstractRouter> newerTimeInterval) {
        MmeRouterConfig mme = null;
        ImsRouterConfig ims = null;
        DpiRouterConfig dpi = null;
        FiveDpiRouterConfig fiveDpi = null;
        FiveNxRouterConfig nx = null;
        GnRouterConfig gn = null;
        if (type==1){
            mme = this.routerConfig.getMme();
        }
        if (type==2){
            ims = this.routerConfig.getIms();
        }
        if (type==3){
            dpi = this.routerConfig.getDpi();
        }
        if (type==4){
            fiveDpi = this.routerConfig.getFiveDpi();
        }
        if (type==5){
            nx = this.routerConfig.getNx();
        }
        if (type==6){
            gn = this.routerConfig.getGn();
        }
        if (effectiveTime !=null && !"".equals(effectiveTime)){
            if (type==1){
                mme.setEffectiveTime(effectiveTime);
            }
            if (type==2){
                ims.setEffectiveTime(effectiveTime);
            }
            if (type==3){
                dpi.setEffectiveTime(effectiveTime);
            }
            if (type==4){
                fiveDpi.setEffectiveTime(effectiveTime);
            }
            if (type==5){
                nx.setEffectiveTime(effectiveTime);
            }
            if (type==6){
                gn.setEffectiveTime(effectiveTime);
            }
        }else{
            log.info("The old rule expiration time is not configured. Use default old rule expiration time configuration!!");
        }
        if (oldTimeInterval!=null&&oldTimeInterval.size()>0){
            List<? extends AbstractRouter> abstractRouters = filterOldTimeInterval(oldTimeInterval);
            if (type==1){
                mme.setOldTimeInterval((List<OldTimeIntervalProperties>) abstractRouters);
            }
            if (type==2){
                ims.setOldTimeInterval((List<OldTimeIntervalProperties>) abstractRouters);
            }
            if (type==3){
                dpi.setOldTimeInterval((List<OldTimeIntervalProperties>) abstractRouters);
            }
            if (type==4){
                fiveDpi.setOldTimeInterval((List<OldTimeIntervalProperties>) abstractRouters);
            }
            if (type==5){
                nx.setOldTimeInterval((List<OldTimeIntervalProperties>) abstractRouters);
            }
            if (type==6){
                gn.setOldTimeInterval((List<OldTimeIntervalProperties>) abstractRouters);
            }
        }else{
            log.info("Old rules are not configured. Use default old rule configuration!!");
        }
        if (newerTimeInterval!=null&&newerTimeInterval.size()>0){
            List<? extends AbstractRouter> abstractRouters = filterOldTimeInterval(newerTimeInterval);
            if (type==1){
                mme.setNewerTimeInterval((List<NewerTimeIntervalProperties>) abstractRouters);
            }
            if (type==2){
                ims.setNewerTimeInterval((List<NewerTimeIntervalProperties>) abstractRouters);
            }
            if (type==3){
                dpi.setNewerTimeInterval((List<NewerTimeIntervalProperties>) abstractRouters);
            }
            if (type==4){
                fiveDpi.setNewerTimeInterval((List<NewerTimeIntervalProperties>) abstractRouters);
            }
            if (type==5){
                nx.setNewerTimeInterval((List<NewerTimeIntervalProperties>) abstractRouters);
            }
            if (type==6){
                gn.setNewerTimeInterval((List<NewerTimeIntervalProperties>) abstractRouters);
            }
        }else {
            log.info("No new rules configured. Use default new rule configuration!!");
        }
    }

    private AbstractRouterConfig getAbstractRouterConfig(int type) {
        if (type==1){
            return this.routerMapper.findMmeRouterConfig();
        }
        if (type==2){
            return this.routerMapper.findImsRouterConfig();
        }
        if (type==3){
            return this.routerMapper.findDpiRouterConfig();
        }
        if (type==4){
            return this.routerMapper.findFiveDpiRouterConfig();
        }
        if (type==5){
            return this.routerMapper.findFiveNxRouterConfig();
        }
        if (type==6){
            return this.routerMapper.findGnRouterConfig();
        }
        return null;
    }

    private List<? extends AbstractRouter> filterOldTimeInterval(List<? extends AbstractRouter> list) {
        return Optional.ofNullable(list).orElse(null).stream()
                .filter(l -> l.getStartDate()!=99 && l.getEndDate()!=99)
                .collect(Collectors.toList());
    }
}
