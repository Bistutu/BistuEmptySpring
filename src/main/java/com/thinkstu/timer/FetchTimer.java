package com.thinkstu.timer;

import com.thinkstu.entity.*;
import com.thinkstu.utils.*;
import jakarta.annotation.*;
import lombok.extern.slf4j.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author : ThinkStu
 * @作用 : 定时获取空教室数据，
 * @校区代码 : 1小营，2健翔桥，3清河，10昌平
 * @描述 : 程序在开始的时候会执行一次，然后每天 6 点、12 点各执行一次
 * @since : 2023/4/4, 08:18, 周二
 **/
@Slf4j
@Component
public class FetchTimer {
    private final ExecutorService executor;
    private final CookieUtils cookieUtils;
    private final GenerateJSON generateJSON;
    private final RequestUtils requestUtils;
    private HashMap<Integer, int[]> ruleMap;    // 时段划分的规则集

    // 使用构造函数注入依赖
    public FetchTimer(ExecutorService executor, CookieUtils cookieUtils, GenerateJSON generateJSON, RequestUtils requestUtils) {
        this.executor = executor;
        this.cookieUtils = cookieUtils;
        this.generateJSON = generateJSON;
        this.requestUtils = requestUtils;
    }

    // 每天 6 点、12 点各执行一次
    @Scheduled(cron = "0 0 6,12 * * ?")
    private void autoFetch() throws InterruptedException {
        requestUtils.setCookie(cookieUtils.get());          // 获取并设置 Cookie
        LocalDateTime now = LocalDateTime.now();            // 获取今天的日期

        // for 循环，从 1~8，对应我们定义的时段
        for (int token = 0; token < 5; token++) {
            LocalDateTime  current    = now.plus(token, ChronoUnit.DAYS);
            String         yyyy_MM_dd = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(current);
            String         MM_DD      = DateTimeFormatter.ofPattern("MMdd").format(current);
            CountDownLatch latch      = new CountDownLatch(8);     // 8 个线程，每个线程处理一个时段
            for (int i = 1; i <= 8; i++) {
                int[] rule = ruleMap.get(i);   // 获取时段划分的规则
                executor.execute(() -> {
                    try {
                        // 流程：获取数据 -> 生成 JSON -> 保存 JSON
                        generateJSON.generate1(requestUtils.post(new ParamEntity(yyyy_MM_dd, 1, rule[1], rule[2])), MM_DD, rule[0]);
                        generateJSON.generate2(requestUtils.post(new ParamEntity(yyyy_MM_dd, 2, rule[1], rule[2])), MM_DD, rule[0]);
                        generateJSON.generate3(requestUtils.post(new ParamEntity(yyyy_MM_dd, 3, rule[1], rule[2])), MM_DD, rule[0]);
                        generateJSON.generate10(requestUtils.post(new ParamEntity(yyyy_MM_dd, 10, rule[1], rule[2])), MM_DD, rule[0]);
                    } finally {
                        latch.countDown();  // 线程执行完毕，计数器减一
                    }
                });
            }
            latch.await(); // 等待所有线程执行完毕
            log.info("===》{} 执行完毕！", yyyy_MM_dd);
        }
    }


    // 初始化操作
    @PostConstruct
    void initial() throws Exception {
        // 这里我将时段划分为 8 个，每个时段都有：开始节次与结束节次
        ruleMap = new HashMap<>();
        ruleMap.put(1, new int[]{1, 1, 12});
        ruleMap.put(2, new int[]{2, 1, 5});
        ruleMap.put(3, new int[]{3, 1, 2});
        ruleMap.put(4, new int[]{4, 3, 5});
        ruleMap.put(5, new int[]{5, 6, 9});
        ruleMap.put(6, new int[]{6, 6, 7});
        ruleMap.put(7, new int[]{7, 8, 9});
        ruleMap.put(8, new int[]{8, 10, 12});
        autoFetch();    // 自动执行一次
    }
}
