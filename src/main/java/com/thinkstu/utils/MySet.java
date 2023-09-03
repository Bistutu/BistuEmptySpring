package com.thinkstu.utils;

import org.springframework.stereotype.*;

import java.util.*;

/**
 * @author : ThinkStu
 * @since : 2023/3/4, 14:28, 周六
 **/
@Component
public class MySet<T> extends TreeSet<T> {
    /**
     * 只要存在一个元素就返回 false
     *
     * @param tags
     * @return
     */
    public Boolean notContain(Integer... tags) {
        for (Integer tag : tags) {
            if (this.contains(String.valueOf(tag))) {
                return false;
            }
        }
        return true;
    }
}
