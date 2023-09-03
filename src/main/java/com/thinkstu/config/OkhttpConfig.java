package com.thinkstu.config;

import okhttp3.*;
import org.springframework.context.annotation.*;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.*;

@Configuration
public class OkhttpConfig {
    @Bean
    OkHttpClient okHttpClient() {
        // 作用：解决教务网的SSL证书问题，_.bistu.edu.cn.cer 证书源自教务网，手动下载即可
        try {
            CertificateFactory cf   = CertificateFactory.getInstance("X.509");                 // 证书工厂
            String             path = System.getProperty("user.dir") + "/_.bistu.edu.cn.cer";       // 证书路径
            try (InputStream caInput = new BufferedInputStream(new FileInputStream(path))) {        // 证书输入流
                X509Certificate ca           = (X509Certificate) cf.generateCertificate(caInput);   // 证书
                String          keyStoreType = KeyStore.getDefaultType();           // 密钥库类型
                KeyStore        keyStore     = KeyStore.getInstance(keyStoreType);  // 密钥库
                keyStore.load(null, null);       // 加载空密钥库
                keyStore.setCertificateEntry("ca", ca);    // 设置证书
                // 创建一个信任管理器，信任我们密钥库中的CA
                String              tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();       // 信任管理器工厂
                TrustManagerFactory tmf          = TrustManagerFactory.getInstance(tmfAlgorithm);   // 信任管理器
                tmf.init(keyStore); // 初始化信任管理器
                // 创建一个使用我们的 TrustManager 的 SSLContext
                SSLContext sslContext = SSLContext.getInstance("TLS");      // SSL上下文
                sslContext.init(null, tmf.getTrustManagers(), null);    // 初始化SSL上下文
                return new OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
//                        .addInterceptor(new RetryInterceptor(3, 1000))  // 重试拦截器
                        .build();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
