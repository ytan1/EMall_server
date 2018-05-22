package com.emall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

//Serialize the return value without displaying null key
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {

    private int code;
    private T data;
    private String msg;

    public int getCode() {
        return code;
    }
    public T getData() {
        return data;
    }
    public String getMsg() {
        return msg;
    }

    private ServerResponse (int code){
        this.code = code;
    }
    private ServerResponse (int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    private ServerResponse (int code, T data){
        this.code = code;
        this.data = data;
    }
    private ServerResponse (int code, String msg, T data){
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    //return value not displaying this method
    @JsonIgnore
    public boolean isSuccess(){
        return this.code == ResponseCode.SUCCESS.getCode();
    }

    public static <T> ServerResponse<T> responseBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServerResponse<T> responseBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
    }
    public static <T> ServerResponse<T> responseBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
    }
    public static <T> ServerResponse<T> responseBySuccess(String msg, T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg,data);
    }
    public static <T> ServerResponse<T> responseByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMsg());
    }
    public static <T> ServerResponse<T> responseByError(String errorMsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errorMsg);
    }
    public static <T> ServerResponse<T> responseByError(int code, String errorMsg){
        return new ServerResponse<T>(code, errorMsg);
    }
}

