package com.xusheng94.leyu.admin.common;

import lombok.Getter;
import lombok.Setter;

/**
 * 全局统一响应体
 * @param <T> 返回数据类型
 */
@Getter
@Setter
public class ApiResponse<T> {

    public static String PROJECT_VERSION;

    /** 是否成功 */
    private boolean success;

    /** 状态码 */
    private int code;

    /** 返回数据 */
    private T data;

    /** 提示信息 */
    private String msg;

    /** 系统版本 */
    private String version;

    public ApiResponse() {}

    public ApiResponse(int code, boolean success, T data, String msg) {
        this.success = success;
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.version = PROJECT_VERSION;
    }

    // ------------------- Success -------------------

    /** 成功，带数据，默认 msg = "ok" */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, true, data, "ok");
    }

    /** 成功，带数据和自定义 msg */
    public static <T> ApiResponse<T> success(T data, String msg) {
        return new ApiResponse<>(200, true, data, msg);
    }

    /** 成功，无返回数据，默认 msg = "ok" */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, true, null, "ok");
    }

    /** 成功，无返回数据，自定义 msg */
    public static <T> ApiResponse<T> success(String msg) {
        return new ApiResponse<>(200, true, null, msg);
    }

    // ------------------- Fail -------------------

    /** 失败，带状态码和提示信息，无 data */
    public static <T> ApiResponse<T> fail(int code, String msg) {
        return new ApiResponse<>(code, false, null, msg);
    }

    /** 失败，带状态码、提示信息和返回数据 */
    public static <T> ApiResponse<T> fail(int code, T data, String msg) {
        return new ApiResponse<>(code, false, data, msg);
    }

    /** 失败，默认状态码 500 */
    public static <T> ApiResponse<T> fail(String msg) {
        return new ApiResponse<>(500, false, null, msg);
    }

    /** 失败，默认消息 */
    public static <T> ApiResponse<T> fail(int code) {
        return new ApiResponse<>(code, false, null, getMsgByStatus(code));
    }

    private static String getMsgByStatus(int status) {
        return switch (status) {
            case 401 -> "请重新登录";
            case 403 -> "没有权限";
            default -> "请求失败";
        };
    }
}