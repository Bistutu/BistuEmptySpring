package com.thinkstu.controller;

import com.thinkstu.utils.*;
import lombok.extern.slf4j.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;


/**
 * @author : ThinkStu
 * @since : 2023/5/14, 16:29, 周日
 **/
@Slf4j
@RestController
public class MonitorController {

    Integer count = 1;
    PathInitial pathInitial;

    public MonitorController(PathInitial pathInitial) {
        this.pathInitial = pathInitial;
    }

    @GetMapping("/{campus}/{forward}.json")
    String get1(
            @PathVariable("campus") Integer campus,
            @PathVariable("forward") String forward,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ip) {
        log.info("{}=》第 {} 次访问：{}", ip, count++, forward);    // 访问自增1
        String path = pathInitial.getPath() + campus + File.separator + forward + ".json";
        return new cn.hutool.core.io.file.FileReader(path).readString();
    }


    @GetMapping("/empty/{campus}/{forward}.json")
    String get2(@PathVariable("campus") Integer campus,
                @PathVariable("forward") String forward,
                @RequestHeader(value = "X-Forwarded-For", required = false) String ip) {
        String path = pathInitial.getPath() + campus + File.separator + forward + ".json";

        if (campus == 1 || campus == 2 || campus == 5 || campus == 8) {
            log.info("{}=》第 {} 次访问：{}", ip, count++, forward);    // 访问自增1
            path = pathInitial.getPath() + "1" + File.separator + forward + ".json";
        }
        return new cn.hutool.core.io.file.FileReader(path).readString();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    void refresh() {
        count = 1;
    }
}