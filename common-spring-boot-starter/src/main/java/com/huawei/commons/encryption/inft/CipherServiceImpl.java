/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.commons.encryption.inft;


import com.huawei.commons.encryption.arithmetic.IArithmeticService;
import com.huawei.commons.encryption.arithmetic.SM4ArithmeticService;
import com.huawei.commons.encryption.key.IKeyService;
import com.huawei.commons.encryption.key.KeyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class CipherServiceImpl implements ICipherService {
    private IKeyService keyService;
    private IArithmeticService arithService;

    public CipherServiceImpl(IKeyService keyService, IArithmeticService arithService) {
        this.keyService = keyService;
        this.arithService = arithService;
    }

    public CipherServiceImpl(String adapterUrl, int paramMonth) {
        this(new KeyService(adapterUrl, paramMonth), new SM4ArithmeticService());
    }

    @Override
    public String encrypt(String requestTime, String plaintext) {
        if (!StringUtils.isBlank(requestTime) && !StringUtils.isBlank(plaintext)) {
            String key = this.keyService.getKey(requestTime);
            if (StringUtils.isBlank(key)) {
                log.error("get key value failed");
                return null;
            } else {
                String ciphertext = this.arithService.encrypt(key, plaintext);
                if (StringUtils.isBlank(ciphertext)) {
                    log.info("decrypt ciphertext failed");
                    return null;
                } else {
                    return ciphertext;
                }
            }
        } else {
            return null;
        }
    }

    @Override
    public String decrypt(String requestTime, String ciphertext) {
        if (!StringUtils.isBlank(requestTime) && !StringUtils.isBlank(ciphertext)) {
            String key = this.keyService.getKey(requestTime);
            if (StringUtils.isBlank(key)) {
                log.error("get key value failed");
                return null;
            } else {
                String plaintext = "";

                try {
                    plaintext = this.arithService.decrypt(key, ciphertext);
                } catch (Exception var6) {
                    log.error(var6.getMessage(), var6);
                }

                if (StringUtils.isBlank(plaintext)) {
                    log.info("decrypt ciphertext failed");
                    return null;
                } else {
                    return plaintext.trim();
                }
            }
        } else {
            return null;
        }
    }
}
