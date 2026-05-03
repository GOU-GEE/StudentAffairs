package com.studentaffairs.backend.exception;

import com.studentaffairs.backend.common.Result;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public Result<String> handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex) {
        // 捕获并发冲突异常，返回友好的超卖提示
        return Result.error(409, "当前岗位太火爆啦，被其他人抢先了一步，请刷新后重试！");
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception ex) {
        ex.printStackTrace();
        return Result.error(500, "服务器内部错误: " + ex.getMessage());
    }
}
