package com.example.template.common;

public class ApiResponse<T> {
    private int code;
    private T data;

    private String msg;

    public ApiResponse() {}

    public ApiResponse(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    // 不带 msg 参数，默认 msg = "ok"
    public ApiResponse(int code, T data) {
        this(code, data, "ok"); // 调用上面的构造器
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, data);
    }

    public static <T> ApiResponse<T> fail(int code, T data, String msg) {
        return new ApiResponse<>(code, data, msg);
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}