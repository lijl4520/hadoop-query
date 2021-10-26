/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.encryption;

import com.huawei.commons.service.EncryAndDecryService;
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

    @PostMapping(value = "/sm4Ecnew/{str}")
    public String sm4Ecnew(@PathVariable String str){
        return encryAndDecryService.sm4Ecnew(str);
    }

    @PostMapping(value = "/sm4Decryp/{str}")
    public String sm4Decryp(@PathVariable String str){
        return encryAndDecryService.sm4Decryp(str);
    }
}
