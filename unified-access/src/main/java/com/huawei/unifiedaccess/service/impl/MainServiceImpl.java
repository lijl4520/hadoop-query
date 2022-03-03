/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.unifiedaccess.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.huawei.commons.domain.code.ResultCode;
import com.huawei.commons.domain.resp.CommonResult;
import com.huawei.unifiedaccess.config.HbaseCientAddressConfig;
import com.huawei.unifiedaccess.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author Lijl
 * @ClassName MainServiceImpl
 * @Description TODO
 * @Date 2021/11/23 17:32
 * @Version 1.0
 */
@Slf4j
@Service
public class MainServiceImpl implements MainService {

    private final RestTemplate restTemplate;
    private final Map<String,String> hbaseCientAddressMap;
    private final ThreadPoolTaskExecutor executorService;;
    private final String J_Z_H = "JZH_";

    public MainServiceImpl(RestTemplate restTemplate, HbaseCientAddressConfig hbaseCientAddressConfig,
                           ThreadPoolTaskExecutor executorService) {
        this.restTemplate = restTemplate;
        this.hbaseCientAddressMap = hbaseCientAddressConfig.getAddress();
        this.executorService = executorService;
    }

    @Override
    public CommonResult doAction(String model, String action, JSONObject jsonObject, HttpHeaders httpHeaders) {
        String province = jsonObject.getString("province");
        List<String> addressList = new ArrayList<>();
        String aCase = model.toUpperCase(Locale.ROOT);
        if (StringUtils.hasLength(province)){
            String addressStr = hbaseCientAddressMap.get(province.toUpperCase(Locale.ROOT));
            if (!StringUtils.hasLength(addressStr)){
                addressStr = hbaseCientAddressMap.get(J_Z_H + aCase);
            }
            if (StringUtils.hasLength(addressStr)){
                addressList.add(new StringBuffer(addressStr).append("/")
                        .append(model).append("/").append(action).toString());
            }
        }else{
            hbaseCientAddressMap.forEach((k,v)->{
                String uri = null;
                if (k.equals(J_Z_H+aCase)){
                    uri = v;
                }else{
                    if (!k.equals(J_Z_H+"MME")&&!k.equals(J_Z_H+"GN")){
                        uri = v;
                    }
                }
                if (uri!=null){
                    addressList.add(new StringBuffer(uri).append("/")
                            .append(model).append("/").append(action).toString());
                }
            });
        }
        if (addressList.size()==0){
            return CommonResult.failed("未找到接口");
        }
        List<Future<CommonResult>> futures = new ArrayList<>();
        addressList.forEach(requestUri ->{
            Future<CommonResult> submitRet = executorService.submit(() -> {
                try {
                    HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toJSONString(),httpHeaders);
                    ResponseEntity<CommonResult> ret = restTemplate.exchange(requestUri,
                            HttpMethod.POST,
                            httpEntity,
                            CommonResult.class);
                    if (ret.getStatusCodeValue()==200){
                        return ret.getBody();
                    }
                } catch (RestClientException e) {
                    e.printStackTrace();
                    log.error("请求 url:{}失败",requestUri);
                }
                return CommonResult.failed("查询失败");
            });
            futures.add(submitRet);
        });
        List dataList = new ArrayList();
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        if (futures.size()>0){
            futures.forEach(future ->{
                try {
                    CommonResult commonResult = future.get(3, TimeUnit.MINUTES);
                    if (commonResult!=null&&commonResult.getCode()==0){
                        Object data = commonResult.getData();
                        if(data!=null){
                            dataList.addAll((List)data);
                        }
                    }else{
                        atomicBoolean.set(false);
                    }
                } catch (InterruptedException e) {
                    atomicBoolean.set(false);
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    atomicBoolean.set(false);
                    log.error("线程执行异常，异常原因：{}",e.getMessage());
                } catch (TimeoutException e) {
                    atomicBoolean.set(false);
                    log.error("线程执行超时，超时异常原因：{}",e.getMessage());
                }
            });
            if (atomicBoolean.get()){
                return CommonResult.success(dataList);
            }else{
                return CommonResult.failed(ResultCode.PART_SUCCESS,dataList);
            }
        }
        return CommonResult.failed("未找到响应的查询节点");
    }
}
