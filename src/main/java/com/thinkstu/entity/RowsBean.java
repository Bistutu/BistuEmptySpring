package com.thinkstu.entity;

import lombok.*;

@NoArgsConstructor
@Data
public class RowsBean {
    // 此处直接继承 EmptyEntity
    private String XXXQDM;         // 学校校区
    private String XXXQDM_DISPLAY; // 学校校区
    private String KXRQ;           // 空闲日期
    private String JASMC;          // 教室名称
    private String JASLXDM;        // 教室类型
    private String JSJC;           // 结束节次
    private String KXSJ;           // 时间段
    private String KSJC;           // 空闲节次
    private String SKZWS;          // 上课座位数
    private String KSZWS;          // 考试座位数
    private String KSSKZWS;        // 开始考试座位数
    private String JSKSZWS;        // 结束考试座位数
    private String KSKSZWS;        // 开始上课座位数
    private String JSSKZWS;        // 结束上课座位数
    private String JASLXDM_DISPLAY;// 教室类型
    private String KXJC;           // 空闲节次
    private String JXLDM;          // 教学楼代码
    private String JXLDM_DISPLAY;  // 教学楼代码对应名称
}