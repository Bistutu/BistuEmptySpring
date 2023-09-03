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
public class D {
    PathInitial path;
    RequestUtils requestUtils;
    GetFormat getFormat = new GetFormat();

    public D(PathInitial path, RequestUtils requestUtils) {
        this.path = path;
        this.requestUtils = requestUtils;
    }


    public void fetch(String yyyyMmDd, String md) throws FileNotFoundException {
        int           KSJC = 0, JSJC = 0, time = 0;
        StringBuilder sb   = new StringBuilder().append("[");
        while (time < 1000) {
            time = getFormat.time(time, sb, KSJC, JSJC);    //  生成时间戳
            KSJC = getFormat.K;
            JSJC = getFormat.J;
            ParamEntity param = new ParamEntity(yyyyMmDd, 10, KSJC, JSJC);
            String      data  = requestUtils.post(param);
            int         begin = data.indexOf("rows") + 6;
            int         end   = data.lastIndexOf("]");
            data = data.substring(begin, end + 1);
            //
            List<RowsBean>    userList        = JSON.parseArray(data, RowsBean.class); // userList是原始JSON的对象数组
            ArrayList<String> emptyClassArray = new ArrayList<>();
            String            emptyClass;
            for (RowsBean row : userList) {
                emptyClass = row.getJASMC();
                if (!emptyClass.matches(".*报.*")
                        && !emptyClass.matches(".*XXA-301.*")
                        && !emptyClass.matches(".*(WLA-(10[123]|30[1259]))|(WLC-112).*")) {
                    emptyClassArray.add(emptyClass);
                }
            }
            Collections.sort(emptyClassArray);  // 排序
            getFormat.start(sb, KSJC, JSJC, time, emptyClassArray, ".*WLA.*", "文理楼A\uD83D\uDCD4", 4);
            getFormat.start(sb, KSJC, JSJC, time, emptyClassArray, ".*WLB.*", "文理楼B\uD83D\uDCD4", 4);
            getFormat.start(sb, KSJC, JSJC, time, emptyClassArray, ".*WLC.*", "文理楼C\uD83D\uDCD4", 4);
            getFormat.start(sb, KSJC, JSJC, time, emptyClassArray, ".*XXA.*", "信息楼A\uD83D\uDCBB", 4);
            getFormat.start(sb, KSJC, JSJC, time, emptyClassArray, ".*XXB.*", "信息楼B\uD83D\uDCBB", 4);
            getFormat.start(sb, KSJC, JSJC, time, emptyClassArray, ".*XXC.*", "信息楼C\uD83D\uDCBB", 4);
            getFormat.start_no(sb, KSJC, JSJC, time, emptyClassArray, ".*XXD.*", "信息楼D\uD83D\uDCBB", 4);
            time = getFormat.switchTime(time);
        }
        PrintWriter printWriter = new PrintWriter(path.getPath() + "/4/4" + md + ".json");
        sb.append("]");
        printWriter.print(sb);
        printWriter.close();
    }
}
