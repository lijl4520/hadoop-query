/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author Lijl
 * @ClassName SignUtil
 * @Description AK/SK
 * @Date 2021/11/11 15:38
 * @Version 1.0
 */
public class SignUtil {

    private final static String appSecret = "9ZLEzugQHfQd11vS8pd68lxzA";

    /**
     * @Author lijiale
     * @MethodName wrapperHeader
     * @Description 通过请求参数，包装请求header信息（含签名信息）
     * @Date 16:14 2021/11/11
     * @Version 1.0
     * @param reqParam
     * @return: {sign=02C89AD7CEC9C05831520015CD7C3413F1DE03822D2DA015A7B353B7E7F38E7D, nonce=6b10f2ee-aba6-4032-bc9f-ca82c76b30d1, TimeStamp=1636684729852}
    **/
    public static Map<String, Object> wrapperHeader(Map<String, Object> reqParam) {
        Long ts = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString();
        Map<String, Object> header = new HashMap<>();
        //进行接口调用时的时间戳，即当前时间戳（毫秒），服务端会校验时间戳，例如时间差超过30秒则认为请求无效，防止重复请求的攻击
        header.put("TimeStamp", ts);
        //每个请求提供一个唯一的标识符，服务器能够防止请求被多次使用
        header.put("nonce", nonce);
        //按签名算法获取sign
        String sign = getSign(appSecret, ts, nonce, reqParam);
        header.put("sign", sign);
        return header;
    }

    /**
     * @Author lijiale
     * @MethodName getSign
     * @Description 按签名算法获取sign
     * @Date 16:04 2021/11/11
     * @Version 1.0
     * @param appSecret
     * @param ts
     * @param nonce
     * @param reqParam
     * @return: java.lang.String
    **/
    private static String getSign(String appSecret, Long ts, String nonce, Map<String, Object> reqParam) {
        // 计算签名规则：sign = HMACSHA256("ts=1623388123195&noce=d50e301d-ee2c-446e-8f28-013f0fee09fb&appSecret=1ZLAzEgQHfBd19vSapdL8lxzA&1=2&1=2")
        // 1.请求参数key升序
        // 2.待加密字符串
        StringBuffer s = new StringBuffer();
        s.append("&ts=").append(ts).append("&noce=").append(nonce).append("&appSecret=").append(appSecret);
        reqParam.forEach((k, v) -> s.append("&").append(k).append("=").append(v));
        // 3.对待加密字符串进行加密(对字符串HMACSHA256处理，得到sign值)
        try {
            return HMACSHA256(s.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Author lijiale
     * @MethodName checkReqInfo
     * @Description 验证请求是否有效
     * @Date 10:36 2021/11/12
     * @Version 1.0
     * @param ts
     * @param nonce
     * @param sign
     * @param reqParam
     * @return: 是否有效(方便测试我用Boolean,可根据业务需要，返回对应错误信息，不一定用Boolean)
    **/
    public static Boolean checkReqInfo(Long ts, String nonce, String sign,Map<String, Object> reqParam) {
        String srvSign = getSign(appSecret, ts, nonce, reqParam);
        // 目前能想到的安全验证就这些，或许大家还能想到其他验证，让接口更加安全
        return sign.equalsIgnoreCase(srvSign);
    }

    /**
     * @Author lijiale
     * @MethodName HMACSHA256
     * @Description HMAC-SHA256算法
     * @Date 10:32 2021/11/12
     * @Version 1.0
     * @param data
     * @return: java.lang.String
    **/
    public static String HMACSHA256(String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(appSecret.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    public static void main(String[] args) {
        Map<String, Object> reqParam = new HashMap<String, Object>();
        reqParam.put("1", "2");
        reqParam.put("2", "1");
        //请求头(行sign值等信息)
        Map<String, Object> reqHeader = wrapperHeader(reqParam);
        System.out.println(reqHeader);
        // ==================客户端发起请求，参数param,并把header带入请求中

        // ============================服务器端，收到请求
        // 1.验证请求信息
        // 2处理业务逻辑
        // 3.返回数据到客户端
        long ts = (long) reqHeader.get("TimeStamp");
        String nonce = (String) reqHeader.get("nonce");
        String sign = (String) reqHeader.get("sign");
        Boolean valid = checkReqInfo(ts,nonce,sign,reqParam);
        if (valid){
            System.out.println("有效请求，继续处理...");
        }else {
            System.out.println("无效");
        }
    }
}
