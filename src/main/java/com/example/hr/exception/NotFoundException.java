package com.example.hr.exception;

/** 찾는 데이터가 없을 때 (HTTP 404) */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
