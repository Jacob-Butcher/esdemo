package com.example.esDemo.model;

public class AppResult <T>{
    private T data;

    private boolean isSuccess;

    private String msg;

    private String code;

    public AppResult(boolean isSuccess, String msg, String code, T data){
        this.isSuccess = isSuccess;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public AppResult(boolean isSuccess, String msg, String code){
        this.isSuccess = isSuccess;
        this.code = code;
        this.msg = msg;
    }

    public static <T> AppResult<T> isSuccess(T data){
        return new AppResult<>(true, "", "200", data);
    }
    public static <T> AppResult<T> isFail(String msg){
        return new AppResult<>(false, msg, "500");
    }
    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        this.isSuccess = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
