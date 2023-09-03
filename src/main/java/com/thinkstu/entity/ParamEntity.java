package com.thinkstu.entity;

import lombok.*;

/**
 * @author : ThinkStu
 * @since : 2023/4/4, 10:27, 周二
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamEntity {
    /**
     * @date : 日期（字符串格式），如 2023-04-04
     * @XXXQDM : 校区，如 1
     * @KSJC : 开始节次，如 1
     * @JSJC : 结束节次，如 2
     */
    String date;
    Integer XXXQDM;
    Integer KSJC;
    Integer JSJC;

    public ParamEntity setTime(Integer KSJC, Integer JSJC) {
        this.KSJC = KSJC;
        this.JSJC = JSJC;
        return this;
    }

    public ParamEntity(String date, Integer XXXQDM) {
        this.date = date;
        this.XXXQDM = XXXQDM;
    }
}
