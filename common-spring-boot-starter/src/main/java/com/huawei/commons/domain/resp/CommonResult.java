/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.domain.resp;

import com.huawei.commons.domain.code.IErrorCode;
import com.huawei.commons.domain.code.ResultCode;

/**
 * @Author Lijl
 * @ClassName CommonResult
 * @Description 返回响应实体
 * @Date 2021/9/13 10:45
 * @Version 1.0
 */
public class CommonResult<T> {

    /**
     * 状态码
     */
    private long code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 封装数据
     */
    private T data;

    protected CommonResult(){
    }

    protected CommonResult(long code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * @Author lijiale
     * @MethodName success
     * @Description 成功返回结果
     * @Date 11:14 2021/9/13
     * @Version 1.0
     * @param data 数据
     * @return: null
     **/
    public static <T> CommonResult<T> success(T data){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(),data);
    }

    /**
     * @Author lijiale
     * @MethodName success
     * @Description 成功返回结果
     * @Date 11:16 2021/9/13
     * @Version 1.0
     * @param data 数据
     * @param message 提示信息
     * @return: com.huawei.xdrs.domain.api.CommonResult<T>
    **/
    public static <T> CommonResult<T> success(T data,String message){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(),message,data);
    }

    /**
     * @Author lijiale
     * @MethodName failed
     * @Description 失败返回结果
     * @Date 11:18 2021/9/13
     * @Version 1.0
     * @param errorCode 错误码
     * @return: com.huawei.xdrs.domain.api.CommonResult<T>
    **/
    public static <T> CommonResult<T> failed(IErrorCode errorCode){
        return new CommonResult<T>(errorCode.getCode(),errorCode.getMessage(),null);
    }

    /**
     * @Author lijiale
     * @MethodName failed
     * @Description 失败返回结果
     * @Date 11:19 2021/9/13
     * @Version 1.0
     * @param errorCode 错误码
     * @param message 错误信息
     * @return: com.huawei.xdrs.domain.api.CommonResult<T>
    **/
    public static <T> CommonResult<T> failed(IErrorCode errorCode,String message) {
        return new CommonResult<T>(errorCode.getCode(), message, null);
    }

    /**
     * @Author lijiale
     * @MethodName failed
     * @Description 失败返回结果
     * @Date 11:19 2021/9/13
     * @Version 1.0
     * @param message 提示信息
     * @return: com.huawei.xdrs.domain.api.CommonResult<T>
    **/
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * @Author lijiale
     * @MethodName failed
     * @Description 失败返回结果
     * @Date 11:20 2021/9/13
     * @Version 1.0
     * @param
     * @return: com.huawei.xdrs.domain.api.CommonResult<T>
    **/
    public static <T> CommonResult<T> failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * @Author lijiale
     * @MethodName validateFailed
     * @Description 参数验证失败返回结果
     * @Date 11:20 2021/9/13
     * @Version 1.0
     * @param
     * @return: com.huawei.xdrs.domain.api.CommonResult<T>
    **/
    public static <T> CommonResult<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * @Author lijiale
     * @MethodName validateFailed
     * @Description 参数验证失败返回结果
     * @Date 11:21 2021/9/13
     * @Version 1.0
     * @param message 提示信息
     * @return: com.huawei.xdrs.domain.api.CommonResult<T>
    **/
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * @Author lijiale
     * @MethodName dataExcess
     * @Description 数据过载返回结果
     * @Date 11:26 2021/9/13
     * @Version 1.0
     * @param
     * @return: com.huawei.xdrs.domain.api.CommonResult<T>
    **/
    public static <T> CommonResult<T> dataExcess(){
        return failed(ResultCode.DATA_EXCESS);
    }

    /**
     * @Author lijiale
     * @MethodName dataExcess
     * @Description 数据过载返回结果
     * @Date 11:26 2021/9/13
     * @Version 1.0
     * @param message 提示信息
     * @return: com.huawei.xdrs.domain.api.CommonResult<T>
    **/
    public static <T> CommonResult<T> dataExcess(String message){
        return new CommonResult<T>(ResultCode.DATA_EXCESS.getCode(), message,null);
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
