package com.huawei.commons.config;

import com.huawei.commons.domain.resp.CommonResult;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @Author Lijl
 * @ClassName AuthAspect
 * @Description 切面
 * @Date 2021/10/15 16:42
 * @Version 1.0
 */
@Component
@Aspect
public class AuthAspect {

    @Pointcut(value = "@annotation(com.huawei.commons.domain.annotation.LoadPointcut)")
    public void executePointcut(){}

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
}
