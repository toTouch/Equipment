package com.xiliulou.afterserver.exception;

import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

/**
 * @ClassName : DeliverExceptionHandler
 * @Description : 异常处理类
 * @Author : YG
 * @Date: 2020-04-23 14:11
 */
@Slf4j
@RestControllerAdvice
public class AfterServiceExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(CustomBusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public R HandelCustomBusinessException(CustomBusinessException e) {
        log.error("自定义业务异常信息 ex={}", e.getMessage(), e);
        return R.failMsg(e.getMsg());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R handleException(Exception ex) {
        log.error("未知异常 ex={}", ex.getMessage(), ex);

        return R.failMsg(ex.getMessage());
    }

    /**
     * 拒绝授权异常
     */
    @ExceptionHandler(CusTomBusinessAccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R HandelCusTomBusinessAccessDeniedException(CusTomBusinessAccessDeniedException e) {
        log.error("拒绝授权异常 ex={}", e.getMessage(), e);
        return R.failMsg(e.getMsg());
    }

    /**
     * validation Exception
     *
     * @param exception
     * @return R
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleBodyValidException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        log.error("参数绑定异常,ex = {}", fieldErrors.get(0).getDefaultMessage());
        return R.failMsg(fieldErrors.get(0).getDefaultMessage());
    }

    /**
     * validation Exception
     *
     * @param exception
     * @return R
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {

        log.error("文件上传异常,ex = {}", "文件过大!");
        return R.failMsg("上传文件大小不能超过5MB!");
    }

}
