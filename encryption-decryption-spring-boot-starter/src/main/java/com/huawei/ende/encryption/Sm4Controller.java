/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.ende.encryption;

import com.huawei.ende.encryption.arithmetic.SM4ArithmeticService;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Lijl
 * @ClassName Sm4Controller
 * @Description Sm4加解密
 * @Date 2021/10/25 17:11
 * @Version 1.0
 */
@RestController
public class Sm4Controller {

    private EncryAndDecryService encryAndDecryService;

    @Autowired
    public void setEncryAndDecryService(EncryAndDecryService encryAndDecryService) {
        this.encryAndDecryService = encryAndDecryService;
    }

    @PostMapping(value = "sm4/encryption/{str}")
    public String sm4Ecnew(@PathVariable String str){
        return encryAndDecryService.sm4Ecnew(str);
    }

    @PostMapping(value = "sm4/decrypt/{str}")
    public String sm4Decryp(@PathVariable String str){
        return encryAndDecryService.sm4Decryp(str);
    }

    @PostMapping(value = "/testDecryp")
    public void testDecryp(){
        String deOutString = null;
        try {
            deOutString = (new SM4ArithmeticService()).decrypt("20203sg&hsB33*i6", "000f0b69c3322205aee1759505bb4d3e");
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        System.out.println("SM4解密结果：" + deOutString);
    }
}
