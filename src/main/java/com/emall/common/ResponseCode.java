package com.emall.common;

public enum ResponseCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    LOGIN(10, "NEED_LOGIN"),
    ILLEGAL_ARGS(2, "ILLEGAL ARGUMENTS");

    private final int code;
    private final String msg;

    private ResponseCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
