package com.thinkstu.entity;

import lombok.extern.slf4j.*;

import java.net.*;

@Slf4j
public class BistuLoginEntityBuilder {
    private String loginUrl;
    // 是否需要验证码
    private String needcaptchaUrl;
    // 验证码链接
    private String captchaUrl;
    private String host;
    private String protocol;

    public BistuLoginEntityBuilder loginUrl(String loginUrl) {
        URL url = null;
        try {
            url = new URL(loginUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.host = url.getHost();
        this.protocol = url.getProtocol();
        this.loginUrl = loginUrl;
        return this;
    }

    public BistuLoginEntity build() {
        this.needcaptchaUrl = protocol + "://" + host + "/authserver/checkNeedCaptcha.htl";
        this.captchaUrl = protocol + "://" + host + "/authserver/getCaptcha.htl";
        return new BistuLoginEntity(loginUrl, needcaptchaUrl, captchaUrl);
    }
}
