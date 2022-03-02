package com.huawei.commons.config;

import com.alibaba.fastjson.JSONObject;
import com.huawei.commons.domain.resp.CommonResult;
import com.huawei.commons.exception.Asserts;
import com.huawei.commons.util.JsonToMap;
import com.huawei.commons.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
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

    //@Before("executePointcut()")
    public void before(JoinPoint joinPoint){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object[] args = joinPoint.getArgs();
        boolean bl = false;
        JSONObject json = null;
        for (Object arg : args) {
            if (arg instanceof JSONObject){
                bl = true;
                json = (JSONObject) arg;
            }
        }
        if (!bl){
            log.error("请求参数缺失...");
            Asserts.fail("Request parameter missing");
        }
        Map sortedMap = JsonToMap.sortParams(json);
        String timeSta = request.getHeader("TimeStamp");
        String nonce = request.getHeader("nonce");
        String sign = request.getHeader("sign");
        if (timeSta!=null&&StringUtils.hasLength(nonce)
        &&StringUtils.hasLength(sign)){
            String s = map.get(nonce);
            if (s!=null){
                log.error("重复的请求...");
                Asserts.fail("Repeat request");
            }
            map.put(nonce,nonce);
            //开启守护线程 清除请求唯一标识
            executorService.execute(new RemoveMapRunnable(nonce));
            Long timeStamp = Long.parseLong(timeSta);
            long endTime = System.currentTimeMillis();
            if (endTime-timeStamp > l){
                log.error("请求过期失效..");
                Asserts.fail("Request expired");
            }
            if(!SignUtil.checkReqInfo(timeStamp, nonce, sign, sortedMap)){
                Asserts.fail("Authentication failed");
            }
        }else{
            log.error("认证参数缺失..");
            Asserts.fail("Missing authentication parameters");
        }
        log.info("认证成功...");
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
