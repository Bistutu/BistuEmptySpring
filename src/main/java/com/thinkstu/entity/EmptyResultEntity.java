package com.thinkstu.entity;

import lombok.*;

import java.util.*;

/**
 * @author : ThinkStu
 * @since : 2023/4/3, 18:15, 周一
 **/
@NoArgsConstructor
@Data
public class EmptyResultEntity {
    private DatasBean datas;
    private String code;

    @NoArgsConstructor
    @Data
    public static class DatasBean {
        private CxkxjsBean cxkxjs;  // 查询可选教室

        @NoArgsConstructor
        @Data
        public static class CxkxjsBean {
            private int totalSize;
            private int pageNumber;
            private int pageSize;
            private List<RowsBean> rows;
            private ExtParamsBean extParams;

            @NoArgsConstructor
            @Data
            public static class ExtParamsBean {
                private String logId;
                private int code;
                private int totalPage;
            }

            // 主要的教室数据
            RowsBean rowsBean;
        }
    }
}
