package com.gujo.uminity.advice;

import com.gujo.uminity.auth.controller.AuthController;
import com.gujo.uminity.common.response.ErrorResponse;
import com.gujo.uminity.common.response.ResultStatus;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AuthController.class)
@Slf4j
public class AuthExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        ErrorResponse err = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(ResultStatus.FAIL.name())
                .message("입력 값이 올바르지 않습니다.")
                .detail(errors.toString())
                .build();
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(IllegalArgumentException ex) {
        log.error("Business error: {}", ex.getMessage());
        ErrorResponse err = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .code(ResultStatus.FAIL.name())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unhandled error", ex);
        ErrorResponse err = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code(ResultStatus.FAIL.name())
                .message("서버 오류가 발생했습니다.")
                .detail(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
