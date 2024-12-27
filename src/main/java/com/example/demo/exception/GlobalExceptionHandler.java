package com.example.demo.exception;

import com.example.demo.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthorizationExceptionEx.class)
    public Result<String> authorizationException(AuthorizationExceptionEx ex) {
        LOGGER.info("权限校验异常: ", ex);
        return Result.fail(1000, ex.getMessage());
    }

    // handling specific exception
    @ExceptionHandler(ServerException.class)
    public Result<String> serverException(ServerException ex) {
        LOGGER.info("业务服务异常: ", ex);
        return Result.fail(6000, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<String> accessDeniedException(AccessDeniedException ex) {
        LOGGER.info("访问拒绝: ", ex);
        return Result.fail(403, "权限不足");
    }

    // handling global exception
    @ExceptionHandler(Exception.class)
    public Result<String> exception(Exception ex) {
        System.out.println("exceptionHandling = " + ex);
        return Result.fail(500, "服务器内部异常,请稍后重试");
    }
}
