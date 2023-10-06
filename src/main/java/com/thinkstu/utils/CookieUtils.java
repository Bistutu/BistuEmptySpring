package com.thinkstu.utils;

import com.thinkstu.service.*;
import lombok.extern.slf4j.*;
import okhttp3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import javax.net.ssl.*;
import java.io.*;
import java.util.*;

/**
 * @author : ThinkStu
 * @since : 2023/4/4, 08:49, 周二
 * 从文件中读取 cookie
 **/
@Slf4j
@Component
public class CookieUtils {
    OkHttpClient client;
    PathInitial pathUtils;
    LoginService loginService;
    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;

    public CookieUtils(OkHttpClient client, PathInitial pathUtils, LoginService loginService) {
        this.client = client;
        this.pathUtils = pathUtils;
        this.loginService = loginService;
    }

    /**
     * 该 Cookie 类的作用应该为：从文件中读取 Cookie 值，但是不应该连续使用（耗资源）
     */
    public String get() {
        String emptyCookie = "";
        try {
            String         path   = System.getProperty("user.dir") + "/cookie.txt";
            FileReader     reader = new FileReader(path);
            BufferedReader in     = new BufferedReader(reader);
            emptyCookie = in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        emptyCookie = check(emptyCookie);
        return emptyCookie;
    }

    /**
     * 测试：检查 cookie 的值<br>
     * 如果过期我们会更新该 Cookie 的值<br>
     * 该函数的作用：保持 cookie 最新！
     */
    public String check(String cookie) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            // 忽略证书信任
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        OkHttpClient client = builder.build();

        // 测试：尝试导出一次空教室数据
        Request request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/kxjas/modules/kxjscx/cxkxjs.do")
                .method("POST", RequestBody.create(MediaType.parse("text/plain"),
                        "querySetting=[]&pageSize=1&pageNumber=1"))
                .addHeader("Cookie", cookie)
                .build();
        // cookie 的 3 种状态：1、不需要更新，2、更新成功，3、更新失败（发生异常）
        try (Response response = client.newCall(request).execute()) {
            if (!response.header("X-Frame-Options").equals("SAMEORIGIN")) { // 如果 cookie 失效，则更新
                cookie = this.update();
                log.info("===》cookie 更新成功");
            } else {
                log.info("===》cookie 不需要更新");
            }
        } catch (Exception e) {
            log.error("===》发生异常，cookie 更新失败");
            e.printStackTrace();
        }
        return cookie;
    }

    /**
     * 更新 cookie 的值
     */
    String update() throws Exception {
        String emptyCookie = loginService.loginForEmpty(
                new String(Base64.getDecoder().decode(username))
                , new String(Base64.getDecoder().decode(password))
        );
        // 将新 cookie 写入文件
        File       file = new File(pathUtils.getCookie_file());
        FileWriter fw   = new FileWriter(file, false);
        fw.write(emptyCookie);
        fw.close();
        return emptyCookie;
    }
}
