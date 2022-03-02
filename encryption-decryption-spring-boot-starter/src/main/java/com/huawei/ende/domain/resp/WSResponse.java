/**
 * @ProjectName:USKSP
 *
 * Copyright (c) 2015. tuyun@sdic-iot.com. All Rights Reserved.
 *
*/
package com.huawei.ende.domain.resp;

/**
 * @ClassName: KeyResponse
 * @Description: Web
 *
 * @author libratears
 * @date: 2015-3-17 上午1:58:53
 * @version v1.0
 */
public class WSResponse {

    /**
     * @Fields resultCode: 结果码
     */
    private int resultCode;

    /**
     * @Fields secretKey: 密钥
     */
    private String secretKey;

    /**
     * @Fields expireTime: 失效时间
     */
    private String expireTime;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String toString() {
        return "[resultCode: " + this.resultCode +
                ", secretKey: " + this.secretKey +
                ", expireTime: " + this.expireTime  + "]";
    }

    public static final int CODE_SUCCESSS = 0;

    public static final int CODE_DATEPARSEEROR = 101;

    public static final int CODE_NULLOBJECT = 102;

}
