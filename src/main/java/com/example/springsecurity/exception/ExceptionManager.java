package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 전역 예외 처리를 담당하는 클래스를 나타냄(RESTful API 엔드포인트에서 발생하는 예외를 처리할 수 있도록 도와줌)
public class ExceptionManager {

    @ExceptionHandler(AppException.class) // 특정 예외 타입을 처리하는 메서드를 지정
    public ResponseEntity<?> appExceptionHandler(AppException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(e.getErrorCode().name() + " " + e.getMessage()); // 문자열 대신 ResponseObject로 매핑해서 처리할 수 있음
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage()); // UserService에서 넣어줬던 메시지 반환
    }
}
