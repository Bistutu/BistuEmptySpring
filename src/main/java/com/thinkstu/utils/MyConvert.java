package com.thinkstu.utils;


import cn.hutool.core.convert.*;

/**
 * @author : ThinkStu
 * @since : 2023/3/4, 15:23, 周六
 **/
public class MyConvert extends Convert {
    public static <T> MySet<T> toSet(Class<T> elementType, Object value) {
        return (MySet<T>) toCollection(MySet.class, elementType, value);
    }
}
