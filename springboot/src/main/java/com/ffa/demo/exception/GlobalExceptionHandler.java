package com.ffa.demo.exception;

import com.ffa.demo.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 如果抛出的的是ServiceException，则调用该方法
     * @param se 业务异常
     * @return Result
     */
    @ExceptionHandler(com.ffa.demo.exception.ServiceException.class)
    @ResponseBody
    public Result handle(com.ffa.demo.exception.ServiceException se){
        return Result.error(se.getCode(), se.getMessage());
    }

}
