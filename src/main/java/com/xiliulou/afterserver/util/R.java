package com.xiliulou.afterserver.util;

import com.xiliulou.afterserver.constant.ErrorMsg;
import lombok.Data;

/**
 * @author: eclair
 * @Date: 2020/10/13 18:11
 * @Description:
 */
@Data
public class R<T> {
    private Integer code;
    private Long sysTime;
    private T data;
    private String errCode;
    private String errMsg;
    private static final Integer SUCCESS = 0;
    private static final Integer FAIL = 1;

    public static <T> R<T> ok() {
        return returnResult(SUCCESS, null, null, null);
    }

    public static <T> R<T> ok(T data) {
        return returnResult(SUCCESS, data, null, null);
    }

    public static <T> R<T> fail(T data) {
        return returnResult(FAIL, data, null, null);
    }

    public static <T> R<T> failMsg(String msg) {
        return returnResult(FAIL, null, null, msg);
    }

    public static <T> R<T> fail(String errCode, String errMsg) {
        return returnResult(FAIL, null, errCode, errMsg);
    }

    public static <T> R<T> localeFail(String errCode, String lang) {
        return returnResult(FAIL, null, errCode, ErrorMsg.getMessage(errCode));
    }

    public static <T> R<T> localeFail(String errCode, String lang, Object... args) {
        return returnResult(FAIL, null, errCode, ErrorMsg.getMessage(errCode));
    }

    public static <T> R<T> fail(T data, String errCode, String errMsg) {
        return returnResult(FAIL, data, errCode, errMsg);
    }

    private static <T> R<T> returnResult(int result, T data, String errCode, String errMsg) {
        R<T> apiResult = new R<>();
        apiResult.code = result;
        apiResult.errCode = errCode;
        apiResult.errMsg = errMsg;
        apiResult.sysTime = System.currentTimeMillis();
        apiResult.data = data;
        return apiResult;
    }

}
