package com.xiliulou.afterserver.exception;

import com.xiliulou.afterserver.constant.CommonConstants;

/**
 * @ClassName : CusTomBusinessAccessDeniedException
 * @Description : 拒绝授权异常
 * @Author : YG
 * @Date: 2020-04-28 19:57
 */

public class CusTomBusinessAccessDeniedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;


    public CusTomBusinessAccessDeniedException(String msg) {
        this.code = CommonConstants.FAIL;
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
