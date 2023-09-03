package com.thinkstu.utils;


import java.util.*;

/**
 * @author : ThinkStu
 * @since : 2023/4/4, 16:38, 周二
 * @作用 : 比较器，用于排序
 **/
public class IntegerComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        int num1 = Integer.parseInt(o1);
        int num2 = Integer.parseInt(o2);
        return Integer.compare(num1, num2);
    }
}
