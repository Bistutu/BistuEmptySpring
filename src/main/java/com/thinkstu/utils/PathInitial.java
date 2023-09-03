package com.thinkstu.utils;

import jakarta.annotation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.regex.*;

/**
 * Runner：程序初始化时自动创建数据保存目录
 * Utils：工具类，获取路径
 */

@Slf4j
@Data
@Component
public class PathInitial {
    // 下面的地址分别为：开发环境与线上环境的路径
    private String path;
    private String user_dir;
    private String cookie_file;
    @Value("${saved_path}")
    private String saved_path;


    @PostConstruct
    public void get() {
        user_dir = System.getProperty("user.dir");
        cookie_file = user_dir + "/cookie.txt";
        path = checkVirtual() ? user_dir + "/static/data/" : saved_path;
        // 新建目录，确保目录是存在的
        for (int i = 1; i < 5; i++) {
            new File(path + i).mkdirs();
        }
    }

    /**
     * 检测本地环境是否为虚拟机
     */
    public boolean checkVirtual() {
        Pattern pattern = Pattern.compile(".*(Virtual|Cloud|KVM).*");
        try {
            Process        process = Runtime.getRuntime().exec("grep DMI /var/log/dmesg");
            BufferedReader reader  = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String         line    = reader.readLine();
            if (pattern.matcher(line).matches()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
}
