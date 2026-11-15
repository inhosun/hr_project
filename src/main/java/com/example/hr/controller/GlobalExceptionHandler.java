package com.example.hr.controller;

import com.example.hr.exception.BusinessException;
import com.example.hr.exception.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** 모든 컨트롤러에서 발생하는 예외를 한곳에서 처리 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** 없는 직원/부서 조회 -> 404 */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException e, Model model) {
        return errorPage(model, HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** 업무 규칙 위반 -> 400 */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusiness(BusinessException e, Model model) {
        return errorPage(model, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** /employees/abc/edit 처럼 id 자리에 숫자가 아닌 값이 온 경우 -> 400 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(Model model) {
        return errorPage(model, HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
    }

    /** DB 제약 조건 위반 (이메일 동시 등록 등 검증을 빠져나간 경우) -> 409 */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDataIntegrity(Model model) {
        return errorPage(model, HttpStatus.CONFLICT, "이미 사용 중인 값이거나 다른 데이터와 연결되어 있어 처리할 수 없습니다.");
    }

    /** 업로드 최대 크기(5MB) 초과 -> 목록으로 이동하며 안내 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSize(RedirectAttributes ra) {
        ra.addFlashAttribute("error", "업로드 파일 크기는 5MB 이하여야 합니다.");
        return "redirect:/employees";
    }

    private String errorPage(Model model, HttpStatus status, String message) {
        // 스프링 부트 기본 오류 화면과 같은 이름(status, error, message)을 써서 error.html 하나로 공용 사용
        model.addAttribute("status", status.value());
        model.addAttribute("error", status.getReasonPhrase());
        model.addAttribute("message", message);
        return "error";
    }
}
