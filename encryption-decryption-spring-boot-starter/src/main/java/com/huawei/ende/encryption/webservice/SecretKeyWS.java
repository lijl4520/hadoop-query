/**
 * @ProjectName:USKSP
 *
 * Copyright (c) 2015. tuyun@sdic-iot.com. All Rights Reserved.
 *
 */
package com.huawei.ende.encryption.webservice;

import com.huawei.ende.domain.resp.WSResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.text.ParseException;

/**
 * @ClassName: SecretKeyWS
 * @Description: 密钥服务
 *
 * @author libratears
 * @date: 2015-3-16 上午10:17:03
 * @version v1.0
 */
@WebService(targetNamespace = "http://ws.usksp.cmsz.com/")
public interface SecretKeyWS {


    /**
     * @Title: getSecretKey
     * @Description: 获取密钥信息
     *
     * @param requestTime
     *            请求时间
     * @return
     * @throws ParseException
     */
    @WebMethod
    public WSResponse getSecretKey(
            @WebParam(name = "requestTime") String requestTime)
            throws ParseException;
    
    @WebMethod
    public WSResponse getSecretKeyByUser(
            @WebParam(name = "requestTime") String requestTime,@WebParam(name = "userId") String userId)
            throws ParseException;

    /**
     * @Title: getSecretKey
     * @Description: 获取密钥失效时间
     *
     * @param secretKey
     *            请求时间
     * @return
     */
    @WebMethod
    public @WebResult(name = "expireTime")
    String getSecretKeyExpireTime(@WebParam(name = "secretKey") String secretKey);
}