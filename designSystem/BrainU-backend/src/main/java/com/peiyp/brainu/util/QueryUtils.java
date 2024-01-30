package com.peiyp.brainu.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.lang.reflect.Field;

/**
 * @author PeiYP
 * @since 2023年04月12日 13:34
 */
public class QueryUtils {

    public static <T> LambdaQueryWrapper<T> conditionalSearch(T data, LambdaQueryWrapper<T> queryWrapper) {
        Class<?> clazz = data.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("field.getGenericType().toString() = " + field.getGenericType().getTypeName());
            System.out.println("field.getName() = " + field.getName());
        }
        return queryWrapper;
    }

}
