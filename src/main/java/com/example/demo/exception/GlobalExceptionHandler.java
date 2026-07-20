package com.example.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;


@ControllerAdvice
public class GlobalExceptionHandler {

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
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "an unexpected error occurred");
        problemDetail.setProperty("errorCode", "INTERNAL_ERROR");
        return ResponseEntity.status(500).body(problemDetail);
    }
}
