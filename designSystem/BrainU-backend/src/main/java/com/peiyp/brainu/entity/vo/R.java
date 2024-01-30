package com.peiyp.brainu.entity.vo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;

/**
 * @author PeiYP
 * @since 2023年03月16日 17:37
 */
public class R extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    public static final String CODE_TAG = "code";

    /** 返回内容 */
    public static final String MSG_TAG = "msg";

    /** 数据对象 */
    public static final String DATA_TAG = "data";

    public R() {
        super.put(CODE_TAG, 0);
        super.put(MSG_TAG, "成功！");
        super.put(DATA_TAG, null);
    }

    public R(String msg) {
        super.put(CODE_TAG, 0);
        super.put(MSG_TAG, msg);
        super.put(DATA_TAG, null);
    }

    public R(Integer code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        super.put(DATA_TAG, null);
    }

    public R(Integer code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (data != null) {
            super.put(DATA_TAG, data);
        }
    }

    public static R success() {
        return new R();
    }

    public static R success(String msg) {
        return new R(msg);
    }

    public static R success(Integer code, String msg) {
        return new R(code, msg);
    }

    public static R success(Integer code, String msg, Object data) {
        return new R(code, msg, data);
    }

    public static R error() {
        return new R();
    }

    public static R error(String msg) {
        return new R(msg);
    }

    public static R error(Integer code, String msg) {
        return new R(code, msg);
    }

    public static R error(Integer code, String msg, Object data) {
        return new R(code, msg, data);
    }

    public String getCode() {
        return CODE_TAG;
    }

    public R setData(Object data) {
        this.put(DATA_TAG, data);
        return this;
    }

    public<T> T getData(String key, TypeReference<T> typeReference) {
        Object data = this.get(key);
        String jsonString = JSON.toJSONString(data);
        return JSON.parseObject(jsonString, typeReference);
    }

    public<T> T getData(TypeReference<T> typeReference) {
        Object data = this.get("data");
        String jsonString = JSON.toJSONString(data);
        return JSON.parseObject(jsonString, typeReference);
    }



}
