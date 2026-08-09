package com.example.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
        problemDetail.setProperty("errorCode", "USER_NOT_FOUND");
        return problemDetail;
    }

    /*
    {
        "type": "about:blank",
        "title": "Unauthorized",
        "status": 401,
        "detail": "ユーザーが見つかりません",
        "instance": "/api/auth/login",
        "errorCode": "USER_NOT_FOUND"
    }
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(Exception e){
        logger.error("予期しないエラーが発生しました", e);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "an unexpected error occurred");
        problemDetail.setProperty("errorCode", "INTERNAL_ERROR");
        return ResponseEntity.status(500).body(problemDetail);
    }
}
