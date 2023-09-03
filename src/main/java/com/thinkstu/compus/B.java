package com.thinkstu.compus;


import com.alibaba.fastjson2.*;
import com.thinkstu.entity.*;
import com.thinkstu.utils.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

@Slf4j
@Component
public class B {
    PathInitial path;
    RequestUtils requestUtils;
    GetFormat getFormat=new GetFormat();



    public B(PathInitial path, RequestUtils requestUtils) {
        this.path = path;
        this.requestUtils = requestUtils;
    }

    public void fetch(String yyyyMmDd, String md) throws FileNotFoundException {
        int           KSJC = 0, JSJC = 0, time = 0;
        StringBuilder sb   = new StringBuilder().append("[");
        while (time < 1000) {
            time = getFormat.time(time, sb, KSJC, JSJC);
            KSJC = getFormat.K;
            JSJC = getFormat.J;
            // 构造请求参数，发送请求，获得数据
            ParamEntity       param    = new ParamEntity(yyyyMmDd, 2, KSJC, JSJC);
            String            data     = requestUtils.post(param);
            EmptyResultEntity result   = JSON.parseObject(data, EmptyResultEntity.class);
            List<RowsBean>    userList = result.getDatas().getCxkxjs().getRows();
            String            emptyClass;
            // emptyClassArray是为了排除干扰和排序
            ArrayList<String> emptyClassArray = new ArrayList<String>();
            for (RowsBean assist : userList) {
                emptyClass = assist.getJASMC().substring(2)
                        .replaceAll("\\(.*?\\)", "");
                if (emptyClass.matches(".*阶梯.*")) {
                    emptyClass = emptyClass.replaceAll("3-八阶梯", "8八阶梯")
                            .replaceAll("3-七阶梯", "7七阶梯")
                            .replaceAll("3-六阶梯", "6六阶梯")
                            .replaceAll("3-五阶梯", "5五阶梯")
                            .replaceAll("1-四阶梯", "4四阶梯")
                            .replaceAll("1-三阶梯", "3三阶梯")
                            .replaceAll("1-二阶梯", "2二阶梯")
                            .replaceAll("1-一阶梯", "1一阶梯");
                }
                if (emptyClass.matches(".*2-.*")
                        || emptyClass.matches(".*阶梯.*")
                        && !emptyClass.matches(".*102.*")
                        && !emptyClass.matches(".*108.*")
                        && !emptyClass.matches(".*303.*") && !emptyClass.matches(".*407.*")) {
                    emptyClassArray.add(emptyClass);
                }
            }
            Collections.sort(emptyClassArray);  // 排序
            // 生成json
            getFormat.start(sb, KSJC, JSJC, time, emptyClassArray, ".*阶梯.*", "阶梯教室", 1);
            getFormat.start_no(sb, KSJC, JSJC, time, emptyClassArray, ".*2-.*", "第二教学楼", 2);
            time = getFormat.switchTime(time);
        }
        PrintWriter printWriter = new PrintWriter(path.getPath() + "/2/2" + md + ".json");
        sb.append("]");
        printWriter.print(sb);
        printWriter.close();
    }
}


