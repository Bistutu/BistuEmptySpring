package com.thinkstu.service.impl;

import com.thinkstu.process.*;
import com.thinkstu.service.*;
import com.thinkstu.utils.*;
import okhttp3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;

import java.util.*;

/**
 * 获取空教室页 Cookie
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Value("${LOGIN_API}")
    private String LOGIN_API;// 登陆接口
    @Value("${user_agent}")
    private String user_agent;
    OkHttpClient client;
    Request request;
    Response response;

    public LoginServiceImpl(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Map<String, String> login(String username, String password) throws Exception {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new RuntimeException("用户名或者密码为空");
        }
        // 封装参数
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        BistuLoginProcess process = new BistuLoginProcess(LOGIN_API, params);
        return process.login();
    }

    @Override
    public String loginForEmpty(String username, String password) throws Exception {

        // 教务网登录 cookie
        Map<String, String> initialCookie = login(username, password);
        // map 转 myMap
        MyMap<String, String> cookie = MyMap.mapConvertMymap(initialCookie);
        // 第二次发送请求
        MediaType   mediaType = MediaType.parse("application/json");
        RequestBody body      = RequestBody.create(mediaType, "");
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/jwpubapp/gjhwhController/queryAppGjhMapping.do")
                .method("POST", body)
                .addHeader("Cookie", cookie.toString())
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/kxjas/*default/index.do?EMAP_LANG=zh")
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第三次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/jwpubapp/modules/bb/cxjwggbbdqx.do")
                .method("POST", body)
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/kxjas/*default/index.do?EMAP_LANG=zh")
                .addHeader("Cookie", cookie.toString())
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第四次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/funauthapp/api/getAppConfig/kxjas-4768402106681759.do")
                .addHeader("Cookie", cookie.toString())
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第五次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/emappagelog/config/kxjas.do")
                .addHeader("Cookie", cookie.toString())
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第六次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/emapcomponent/schema/getList.do")
                .method("POST", body)
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/kxjas/*default/index.do")
                .addHeader("Cookie", cookie.toString())
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);
        return cookie.emptyCookieForAuthorized();
    }
}
