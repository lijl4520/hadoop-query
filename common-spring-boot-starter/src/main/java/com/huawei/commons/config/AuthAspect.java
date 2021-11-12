package com.huawei.commons.config;

import com.alibaba.fastjson.JSONObject;
import com.huawei.commons.domain.resp.CommonResult;
import com.huawei.commons.exception.Asserts;
import com.huawei.commons.util.JsonToMap;
import com.huawei.commons.util.SignUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Lijl
 * @ClassName AuthAspect
 * @Description 切面
 * @Date 2021/10/15 16:42
 * @Version 1.0
 */
@Slf4j
@Component
@Aspect
public class AuthAspect {

    private static ConcurrentHashMap<String,String> map = new ConcurrentHashMap<>();

    private final long l = 1000*60;

    private ThreadPoolTaskExecutor executorService;

    @Autowired
    public void setExecutorService(ThreadPoolTaskExecutor executorService) {
        this.executorService = executorService;
    }

    @Pointcut(value = "@annotation(com.huawei.commons.domain.annotation.LoadPointcut)")
    public void executePointcut(){}

    @Before("executePointcut()")
    public void before(JoinPoint joinPoint){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object[] args = joinPoint.getArgs();
        JSONObject json = (JSONObject) args[args.length - 1];
        Map sortedMap = JsonToMap.sortParams(json);
        Long timeStamp = Long.parseLong(request.getHeader("TimeStamp"));
        String nonce = request.getHeader("nonce");
        String s = map.get(nonce);
        if (s!=null){
            log.error("重复的请求...");
            Asserts.fail("Repeat request");
        }
        map.put(nonce,nonce);
        //开启守护线程 清除请求唯一标识
        executorService.execute(new RemoveMapRunnable(nonce));
        String sign = request.getHeader("sign");
        if (timeStamp==null||timeStamp<1||StringUtils.isNotEmpty(nonce)
        ||StringUtils.isNotEmpty(sign)){
            long endTime = System.currentTimeMillis();
            if (endTime-timeStamp > l){
                log.error("请求过期失效..");
                Asserts.fail("Request expired");
            }
        }else{
            log.error("认证参数缺失..");
            Asserts.fail("Missing authentication parameters");
        }
        if(!SignUtil.checkReqInfo(timeStamp, nonce, sign, sortedMap)){
            log.error("认证失败,sign={}",sign);
            Asserts.fail("Authentication failed");
        }
        log.info("认证成功...");
    }

    @AfterReturning(value = "executePointcut()",returning = "returnVal")
    public void respMethodCall(Object returnVal){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
        CommonResult ret = (CommonResult) returnVal;
        if (request!=null && response!=null){
            String requestRefId = request.getParameter("requestRefId");
            long code = ret.getCode();
            response.addHeader("requestRefId",requestRefId==null?"":requestRefId);
            response.addHeader("responseRefId","TSRESP_"+getDateStr()+getRandom());
            if (code==0){
                response.addHeader("responseCode","0000");
                response.addHeader("responseMsg","success");
            }else{
                response.addHeader("responseCode","2001");
                response.addHeader("responseMsg","fail");
            }
        }
    }

    private String getDateStr() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateStr = dateTime.format(formatter);
        return dateStr;
    }

    private String getRandom(){
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private class RemoveMapRunnable implements Runnable{
        private String nonce;
        public RemoveMapRunnable(String nonce){
            this.nonce = nonce;
        }
        @Override
        public void run() {
            synchronized (this){
                try {
                    Thread.sleep(l);
                    map.remove(this.nonce);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
