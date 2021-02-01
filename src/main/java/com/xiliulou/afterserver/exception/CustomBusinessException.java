package com.xiliulou.afterserver.exception;

/**
 * @ClassName : CustomBusinessException
 * @Description : 自定义异常   一般用于系统抛出特殊的业务异常
 * @Author : YG
 * @Date: 2020-04-23 10:08
 */
public class CustomBusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;


    public CustomBusinessException(String msg) {

        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}