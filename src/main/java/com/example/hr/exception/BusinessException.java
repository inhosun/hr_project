package com.example.hr.exception;

/** 업무 규칙 위반 (예: 소속 직원이 있는 부서 삭제) (HTTP 400) */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
