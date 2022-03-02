/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.ende.encryption.webservice;

import com.huawei.ende.domain.EncrypDecrypProperties;
import com.huawei.ende.domain.resp.WSResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.text.ParseException;

/**
 * @Author Lijl
 * @ClassName WebServiceClient
 * @Description webservice 客户端
 * @Date 2021/11/29 16:01
 * @Version 1.0
 */
@Slf4j
public class WebServiceClient {

    private static SecretKeyWS secretKeyWS;

    public static SecretKeyWS getInstance(EncrypDecrypProperties authProperties) {
        if (null != secretKeyWS) {
            return secretKeyWS;
        }
        try {
            //"https://10.246.85.8:8051/USKSP/services/SecretKeyWS?wsdl"
            JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
            factoryBean.setAddress(authProperties.getUrl());
            factoryBean.setServiceClass(SecretKeyWS.class);
            secretKeyWS = (SecretKeyWS) factoryBean.create();
            Client proxy = ClientProxy.getClient(secretKeyWS);
            HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
            TLSClientParameters tlsParams = conduit.getTlsClientParameters();
            if (tlsParams == null) {
                tlsParams = new TLSClientParameters();
            }
            tlsParams.setDisableCNCheck(true);
            // 设置keystore
            tlsParams.setKeyManagers(WebServiceClient.getKeyManagers(authProperties));
            // 设置信任证书
            tlsParams.setTrustManagers(WebServiceClient.getTrustManagers(authProperties));
            conduit.setTlsClientParameters(tlsParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return secretKeyWS;
    }

    public static String getSecretKeyByUser(EncrypDecrypProperties authProperties,String requestTime){
        try {
            SecretKeyWS secretKeyWS = WebServiceClient.getInstance(authProperties);
            String userId = authProperties.getUserId();
            WSResponse secretKeyByUser = secretKeyWS.getSecretKeyByUser(requestTime, userId);
            log.info("WSResponse:------>{}",secretKeyByUser.toString());
            return secretKeyByUser.getSecretKey()+","+secretKeyByUser.getExpireTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static KeyManager[] getKeyManagers(EncrypDecrypProperties authProperties) {
        InputStream is = null;
        try {
            // 获取默认的 X509算法
            String alg = KeyManagerFactory.getDefaultAlgorithm();
            // 创建密钥管理工厂
            KeyManagerFactory factory = KeyManagerFactory.getInstance(alg);

            is = resourceLoader(authProperties.getKeystoreFile());
            // 构建以证书相应格式的证书仓库
            KeyStore ks = KeyStore.getInstance(authProperties.getKeystoreType());
            // 加载证书
            String keystorePass = authProperties.getKeystorePass();
            ks.load(is, keystorePass
                    .toCharArray());
            factory.init(ks, keystorePass
                    .toCharArray());
            KeyManager[] keyms = factory.getKeyManagers();
            return keyms;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    private static TrustManager[] getTrustManagers(EncrypDecrypProperties authProperties) {
        // 读取证书仓库输入流
        InputStream is = null;
        try {
            // 信任仓库的默认算法X509
            String alg = TrustManagerFactory.getDefaultAlgorithm();
            // 获取信任仓库工厂
            TrustManagerFactory factory = TrustManagerFactory.getInstance(alg);
            // 读取信任仓库
            is = resourceLoader(authProperties.getTruststoreFile());
            // 密钥类型
            KeyStore ks = KeyStore.getInstance(authProperties.getTruststoreType());
            // 加载密钥
            ks.load(is, authProperties.getTruststorePass()
                    .toCharArray());
            factory.init(ks);
            TrustManager[] tms = factory.getTrustManagers();
            return tms;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static InputStream resourceLoader(String fileFullPath) throws IOException {
        InputStream resourceAsStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(fileFullPath);
        return resourceAsStream;
    }
}
