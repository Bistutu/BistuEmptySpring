package com.thinkstu.entity;

import lombok.*;

// 登录模块的实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BistuLoginEntity {
    private String loginUrl;
    private String needcaptchaUrl;
    private String captchaUrl;
}
