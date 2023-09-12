package com.example.springsecurity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import javax.persistence.Enumerated;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    USERNAME_DUPLICATED(HttpStatus.CONFLICT, ""); // httpStatus, message 초기화

    private HttpStatus httpStatus;
    private String message;
}
