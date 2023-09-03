package com.thinkstu.utils;

import com.thinkstu.entity.*;
import okhttp3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

/**
 * @author : ThinkStu
 * @since : 2023/4/3, 17:44, 周一
 **/
@Component
public class RequestUtils {
    OkHttpClient client;
    CookieUtils cookieUtils;
    private String cookie;
    @Value("${user_agent}")
    String user_agent;
    @Value("${type}")
    String type;
    @Value("${url}")
    String url;
    @Value("${referer}")
    String referer;

    public RequestUtils(OkHttpClient client, CookieUtils cookieUtils) {
        this.client = client;
        this.cookieUtils = cookieUtils;
        cookie = cookieUtils.get();
    }

    public String post(ParamEntity param) {
        // 生成请求参数
        String paramString = new StringBuilder()
                .append("XXXQDM=").append(param.getXXXQDM())
                .append("&JASLXDM=02")  // 教室类型，这里 02 表示多媒体
                .append("&KXRQ=").append(param.getDate())
                .append("&KSJC=").append(param.getKSJC())
                .append("&JSJC=").append(param.getJSJC())
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse(type), paramString))
                .addHeader("User-Agent", user_agent)
                .addHeader("Content-Type", type)
                .addHeader("Referer", referer)
                .addHeader("Cookie", cookie)
                .build();
        String data = "";
        try {
            Response response = client.newCall(request).execute();
            data = response.body().string();
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
