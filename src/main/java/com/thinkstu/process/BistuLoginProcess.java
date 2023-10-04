package com.thinkstu.process;

import com.thinkstu.entity.*;
import com.thinkstu.helper.*;
import com.thinkstu.utils.*;
import lombok.extern.slf4j.*;
import net.sourceforge.tess4j.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.springframework.beans.factory.annotation.*;

import java.io.*;
import java.net.*;
import java.util.*;

@Slf4j
/**
 * NOTCLOUD认证
 */
public class BistuLoginProcess {
    private BistuLoginEntity loginEntity;
    private Map<String, String> map;
    @Value("${user_agent}")
    private String user_agent;


    public BistuLoginProcess(String loginUrl, Map<String, String> params) {
        this.loginEntity = new BistuLoginEntityBuilder()
                .loginUrl(loginUrl)
                .build();
        this.map = params;
    }

    public Map<String, String> login() throws Exception {
        // 忽略证书错误
        HttpsUrlValidator.retrieveResponseFromServer(loginEntity.getLoginUrl());
        // 请求登陆页
        Connection con = Jsoup.connect(loginEntity.getLoginUrl())
                .header("User-Agent", user_agent)
                .followRedirects(true);
        Connection.Response res     = con.execute();
        Document            doc     = res.parse();      // 解析登陆页
        Map<String, String> cookies = res.cookies();    // 全局cookie
        Element             form    = doc.getElementById("pwdLoginDiv");   // 获取登陆表单
        // 处理加密的盐
        Element saltElement = doc.getElementById("pwdEncryptSalt");

        String salt = null;
        if (saltElement != null) {
            salt = saltElement.val();
        }

        // 获取登陆表单里的输入
        Elements inputs = form.getElementsByTag("input");

        String username = this.map.get("username");
        String password = this.map.get("password");

        // 构造post请求参数
        Map<String, String> params = new HashMap<>();
        for (Element e : inputs) {

            // 填充用户名
            if (e.attr("name").equals("username")) {
                e.attr("value", username);
            }

            // 填充密码
            if (e.attr("name").equals("password")) {
                if (salt != null) {
                    e.attr("value", AESHelper.encryptAES(password, salt));
                } else {
                    e.attr("value", password);
                }
            }

            // 排除空值表单属性
            if (e.attr("name").length() > 0) {
                // 排除记住我
                if (e.attr("name").equals("rememberMe")) {
                    continue;
                }
                params.put(e.attr("name"), e.attr("value"));
            }
        }

        // 构造请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Cache-Control", "max-age=0");
        headers.put("Connection", "keep-alive");
        headers.put("Host", new URL(loginEntity.getLoginUrl()).getHost());
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("User-Agent", user_agent);

        // 模拟登陆之前首先请求是否需要验证码接口
        doc = Jsoup.connect(loginEntity.getNeedcaptchaUrl() + "?username=" + username)
                .headers(headers)
                .cookies(cookies)
                .get();
        boolean isNeedCaptcha = doc.body().text().contains("true");
        log.info("=====》是否需要验证码？{}", isNeedCaptcha);
        if (isNeedCaptcha) {
            // 识别验证码后模拟登陆，最多尝试20次
            int time = TesseractOCRHelper.MAX_TRY_TIMES;
            while (time-- > 0) {
                String code = ocrCaptcha(cookies, headers, loginEntity.getCaptchaUrl());
                params.put("captcha", code);
                Map<String, String> cookies2 = bistuSendLoginData(loginEntity.getLoginUrl(), cookies, params);
                if (cookies2 != null) {
                    return cookies2;
                }
            }
            // 执行到这里就代表验证码识别尝试已经达到了最大的次数
            throw new RuntimeException("验证码识别错误，请重试");
        } else {
            // 直接模拟登陆
            return bistuSendLoginData(loginEntity.getLoginUrl(), cookies, params);
        }
    }

    /**
     * 发送登陆请求，返回cookies
     *
     * @param login_url
     * @param cookies
     * @param params
     * @return
     * @throws Exception
     */
    private Map<String, String> bistuSendLoginData(String login_url, Map<String, String> cookies, Map<String, String> params) throws Exception {
        Connection con = Jsoup
                .connect(login_url)
                .header("Origin", "http://wxjw.bistu.edu.cn")
                .header("Referer", "http://wxjw.bistu.edu.cn/authserver/login?service=http://jwxt.bistu.edu.cn/jwapp/sys/emaphome/portal/index.do");
        Connection.Response login = null;
        try {
            login = con.ignoreContentType(true).followRedirects(false).method(Connection.Method.POST).data(params).cookies(cookies).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 重定向代表登陆成功
        if (login.statusCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            // 第一次尝试自动更新cookie
            String location = null;
            try {
                cookies.putAll(login.cookies());
                // 拿到重定向的地址
                location = login.header("location");
                con = Jsoup.connect(location)
                        .ignoreContentType(true)
                        .followRedirects(true)
                        .method(Connection.Method.POST)
                        .header("User-Agent", user_agent)
                        .cookies(cookies);
                // 请求，再次更新cookie
                login = con.execute();
                cookies.putAll(login.cookies());
            } catch (HttpStatusException e) {
                // 第一次自动更新cookie，失败了，携带已获取到的cookie再次尝试/portal/login接口
                location = location.substring(0, location.lastIndexOf('?'));
                con = Jsoup.connect(location)
                        .ignoreContentType(true)
                        .followRedirects(true)
                        .method(Connection.Method.GET)
                        .header("User-Agent", user_agent)
                        .cookies(cookies);
                login = con.execute();
                cookies.putAll(login.cookies());
            }
            // 只有登陆成功才返回cookie
            return cookies;
        } else {
            if (login.statusCode() == HttpURLConnection.HTTP_OK) {
                // 登陆失败
                Document doc = login.parse();
                Element  msg = doc.getElementById("msg");
                if (!msg.text().equals("无效的验证码")) {
                    throw new RuntimeException(msg.text());
                }
            } else {
                // 服务器可能出错
                throw new RuntimeException("教务系统服务器可能出错了，Http状态码是：" + login.statusCode());
            }
        }
        return null;
    }

    /**
     * 处理验证码识别
     *
     * @param cookies
     * @param captcha_url
     * @return
     * @throws IOException
     * @throws TesseractException
     */
    private String ocrCaptcha(Map<String, String> cookies, Map<String, String> headers, String captcha_url) throws IOException, TesseractException {
        while (true) {
            String filePath = System.getProperty("user.dir") + File.separator + System.currentTimeMillis() + ".jpg";
            Connection.Response response = Jsoup.connect(captcha_url)
                    .headers(headers).cookies(cookies)
                    .ignoreContentType(true)
                    .execute();
            // 四位验证码，背景有噪点
            ImageHelper.saveImageFile(ImageHelper.binaryzation(response.bodyStream()), filePath);
            String s = TesseractOCRHelper.doOcr(filePath);

            // 判断是否为字母或数字
            if (judge(s, 4)) {
                return s;
            }
        }
    }

    /**
     * 判断ocr识别出来的结果是否符合条件
     *
     * @param s
     * @param len
     * @return
     */
    private boolean judge(String s, int len) {
        if (s == null || s.length() != len) {
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!(Character.isDigit(ch) || Character.isLetter(ch))) {
                return false;
            }
        }
        return true;
    }
}
