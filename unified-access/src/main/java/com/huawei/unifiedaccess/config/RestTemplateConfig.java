/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.unifiedaccess.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Lijl
 * @ClassName RestTemplateConfig
 * @Description resttemplate 配置类
 * @Date 2021/11/23 10:58
 * @Version 1.0
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(HttpMessageConverter fastJsonHttpMessageConverters) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) {
                //默认处理非200的返回，会抛异常
            }
        });
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        ArrayList<HttpMessageConverter<?>> convertersValid = new ArrayList<>();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter ||
                    messageConverter instanceof MappingJackson2XmlHttpMessageConverter){
                continue;
            }
            convertersValid.add(messageConverter);
        }
        convertersValid.add(fastJsonHttpMessageConverters);
        restTemplate.setMessageConverters(convertersValid);
        return restTemplate;
    }

    /*@Bean(name = "httpsFactory")
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() throws Exception {
        CloseableHttpClient httpClient = createCloseableHttpClient();
        HttpComponentsClientHttpRequestFactory httpsFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        httpsFactory.setReadTimeout(2000);
        httpsFactory.setConnectTimeout(2000);
        return httpsFactory;
    }*/

    /**
     * https协议证书认证
     *
     * @return
     * @throws Exception
     */
    /*private CloseableHttpClient createCloseableHttpClient() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(resourceLoader(cerPath), cerPwd.toCharArray());
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy()).build();
        // 这里的通信协议要根据使用的JDK版本来适配
        SSLConnectionSocketFactory sslfactory = new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslfactory).build();
        PoolingHttpClientConnectionManager phccm = new PoolingHttpClientConnectionManager(registry);
        phccm.setMaxTotal(200);
        CloseableHttpClient httpClient = httpClientBuilder.setSSLSocketFactory(sslfactory).setConnectionManager(phccm).setConnectionManagerShared(true).build();
        return httpClient;
    }*/

    /**
     * @Author lijiale
     * @MethodName resourceLoader
     * @Description 读取文件信息
     * @Date 15:34 2021/11/24
     * @Version 1.0
     * @param fileFullPath
     * @return: java.io.InputStream
    **/
    public InputStream resourceLoader(String fileFullPath) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        return resourceLoader.getResource(fileFullPath).getInputStream();
    }

    @Bean
    public HttpMessageConverter fastJsonHttpMessageConverters() {
        //1、定义一个convert转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //2、添加fastjson的配置信息
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        SerializerFeature[] serializerFeatures = new SerializerFeature[]{
                SerializerFeature.WriteNullNumberAsZero,
                //     List字段如果为null,输出为[],而非null
                SerializerFeature.WriteNullListAsEmpty,
                //    字符类型字段如果为null,输出为"",而非null
                SerializerFeature.WriteNullStringAsEmpty,
                //    Boolean字段如果为null,输出为false,而非null
                SerializerFeature.WriteNullBooleanAsFalse,
                //    Date的日期转换器
                SerializerFeature.WriteDateUseDateFormat,
                //    循环引用
                SerializerFeature.DisableCircularReferenceDetect,
        };
        fastJsonConfig.setSerializerFeatures(serializerFeatures);
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        //3、在convert中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        //4、将convert添加到converters中
        HttpMessageConverter<?> converter = fastConverter;
        return converter;
    }
}
