package com.thinkstu.timer;

import com.thinkstu.compus.*;
import com.thinkstu.utils.*;
import jakarta.annotation.*;
import lombok.extern.slf4j.*;
import okhttp3.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.concurrent.*;


/**
 * @author : ThinkStu
 * @since : 2022/9/20, 1:25 PM, 周二
 **/

@Slf4j
@Component
public class FetchTimerCompatible {
    OkHttpClient client;
    PathInitial path;
    CookieUtils cookieUtils;
    ExecutorService executor;
    A a;
    B b;
    C c;
    D d;

    public FetchTimerCompatible(OkHttpClient client, PathInitial path, CookieUtils cookieUtils, ExecutorService executor, A a, B b, C c, D d) {
        this.client = client;
        this.path = path;
        this.cookieUtils = cookieUtils;
        this.executor = executor;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    //    @Scheduled(initialDelay = 1_000, fixedRate = Integer.MAX_VALUE)
    // 每天 6 点、12 点各执行一次
    @Scheduled(cron = "0 20 6,12 * * ?")
    private void autoFetch() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 5; i++) {
            CountDownLatch latch      = new CountDownLatch(1);
            String         yyyy_MM_dd = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now.plus(i, ChronoUnit.DAYS));
            String         MM_DD      = DateTimeFormatter.ofPattern("Md").format(now.plus(i, ChronoUnit.DAYS));
            executor.submit(() -> {
                try {
                    a.fetch(yyyy_MM_dd, MM_DD);
                    b.fetch(yyyy_MM_dd, MM_DD);
                    c.fetch(yyyy_MM_dd, MM_DD);
                    d.fetch(yyyy_MM_dd, MM_DD);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            });
            latch.await(); // 等待所有线程执行完毕
            log.info("兼容 ===》{} 执行完毕！", yyyy_MM_dd);
        }

    }

    @PostConstruct
    void initial() throws InterruptedException {
        autoFetch();    // 自动执行一次
    }
}
