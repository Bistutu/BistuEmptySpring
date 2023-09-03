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
public class A {
    PathInitial path;
    RequestUtils requestUtils;
    GetFormat getFormat = new GetFormat();

    public A(PathInitial path, RequestUtils requestUtils) {
        this.path = path;
        this.requestUtils = requestUtils;
    }

    public void fetch(String yyyyMmDd, String md) throws FileNotFoundException {
        int KSJC = 0, JSJC = 0, time = 0;    // time是时段
        // 但凡发生一点意外，都不应该更新
        StringBuilder sb = new StringBuilder().append("[");
        while (time < 1000) {
            time = getFormat.time(time, sb, KSJC, JSJC);
            KSJC = getFormat.K;
            JSJC = getFormat.J;
            // 获取数据
            ParamEntity       param    = new ParamEntity(yyyyMmDd, 1, KSJC, JSJC);
            String            data     = requestUtils.post(param);
            EmptyResultEntity result   = JSON.parseObject(data, EmptyResultEntity.class);
            List<RowsBean>    userList = result.getDatas().getCxkxjs().getRows();
            // emptyClassArray是为了排除干扰和排序
            ArrayList<String> emptyClassArray = new ArrayList<String>();
            for (RowsBean row : userList) {
                emptyClassArray.add(row.getJASMC().substring(2));   // 添加教室
            }
            Collections.sort(emptyClassArray);   // 排序
            getFormat.start(sb, KSJC, JSJC, time, emptyClassArray, ".*1-.*", "第一教学楼", 2);
            getFormat.start(sb, KSJC, JSJC, time, emptyClassArray, ".*2-.*", "第二教学楼", 2);
            getFormat.start_no(sb, KSJC, JSJC, time, emptyClassArray, ".*4-.*", "第四教学楼", 2);
            time = getFormat.switchTime(time);  // time的跳转关系
        }
        PrintWriter printWriter = new PrintWriter(path.getPath() + "/1/1" + md + ".json");
        sb.append("]");
        printWriter.print(sb);
        printWriter.close();
    }
}
