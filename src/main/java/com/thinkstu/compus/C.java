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
public class C {
    PathInitial path;
    RequestUtils requestUtils;
    GetFormat getFormat=new GetFormat();

    public C(PathInitial path, RequestUtils requestUtils) {
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
            ParamEntity param = new ParamEntity(yyyyMmDd, 3, KSJC, JSJC);
            String      data  = requestUtils.post(param);
            int         begin = data.indexOf("rows") + 6;
            int         end   = data.lastIndexOf("]");
            data = data.substring(begin, end + 1); // data是初始的的JSON数组
            List<RowsBean> userList = JSON.parseArray(data, RowsBean.class); // userList是原始JSON的对象数组
            // emptyClassArray 是为了排除干扰和排序
            ArrayList<String> emptyClassArray = new ArrayList<String>();
            for (RowsBean row : userList) {
                emptyClassArray.add(row.getJASMC().substring(2));
            }
            Collections.sort(emptyClassArray);   // 排序
            getFormat.start(sb, KSJC, JSJC, time, emptyClassArray, ".*1-.*", "第一教学楼", 2);
            getFormat.start(sb, KSJC, JSJC, time, emptyClassArray, ".*2-.*", "第二教学楼", 2);
            getFormat.start_no(sb, KSJC, JSJC, time, emptyClassArray, ".*3-.*", "第三教学楼", 2);
            time = getFormat.switchTime(time);
        }
        PrintWriter printWriter = new PrintWriter(path.getPath() + "/3/3" + md + ".json");
        sb.append("]");
        printWriter.print(sb);
        printWriter.close();
    }
}


