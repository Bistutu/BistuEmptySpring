package com.thinkstu.utils;

import okhttp3.*;

import java.util.*;

/**
 * @author : ThinkStu
 * @作用: 辅助类，自定义 Map
 * @since : 2023/3/31, 12:52, 周五
 **/
public class MyMap<T, K> extends HashMap<T, K> {
    // Cookie 数组转字符串
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<T, K> entry : this.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }

    // 输出空教室页 Cookie
    public String emptyCookieForAuthorized() {
        StringBuilder sb = new StringBuilder();
        for (Entry<T, K> entry : this.entrySet()) {
            T key = entry.getKey();
            if (key.equals("_WEU") || key.equals("MOD_AUTH_CAS")) {
                sb.append(key).append("=").append(entry.getValue()).append(";");
            }
        }
        return sb.toString();
    }

    // 刷新 Cookie
    public void refresh(Response response) {
        List<String> set_cookie = response.headers("Set-Cookie");
        // 新增或覆盖旧的 cookie
        set_cookie.forEach(e -> {
            int    index = e.indexOf("=");
            String key   = e.substring(0, index);
            String value = e.substring(index + 1, e.indexOf(";"));
            this.put((T) key, (K) value);
        });
    }

    // 静态方法：Map 转 MyMap
    public static MyMap<String, String> mapConvertMymap(Map<String, String> map) {
        MyMap<String, String> myMap = new MyMap<>();
        myMap.putAll(map);
        return myMap;
    }


}
