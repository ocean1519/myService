package com.example.demo.entity;

import java.io.Serial;
import java.io.Serializable;

public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 状态码
    private int code;
    // 消息描述
    private String msg;
    // 数据内容
    private T data;

    public Result() {}

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 成功响应构造器
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    // 失败响应构造器
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    // 错误响应构造器
    public static <T> Result<T> error(String errorMessage) {
        return new Result<>(500, errorMessage, null);
    }

    // getters and setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
