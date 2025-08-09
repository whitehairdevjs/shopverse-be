package org.biz.shopverse.exception;

import lombok.extern.slf4j.Slf4j;
import org.biz.shopverse.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation error occurred: {}", ex.getMessage());
        
        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        // 필드와 무관한 에러 체크
        ex.getBindingResult().getGlobalErrors().forEach(error ->
            errors.put("global", error.getDefaultMessage())
        );

        ApiResponse<Map<String, Object>> errorResponse = ApiResponse.error("Validation Error", "입력값 검증에 실패했습니다.", 400);
        errorResponse.setDetails(errors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
        log.warn("No handler found: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        
        Map<String, Object> details = new HashMap<>();
        details.put("path", request.getDescription(false));
        details.put("exception", ex.getClass().getSimpleName());
        details.put("httpMethod", ex.getHttpMethod());
        details.put("requestURL", ex.getRequestURL());
        
        ApiResponse<String> errorResponse = ApiResponse.error("Not Found", "요청한 엔드포인트를 찾을 수 없습니다.", 404);
        errorResponse.setDetails(details);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        log.warn("Unsupported media type: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("path", request.getDescription(false));
        details.put("exception", ex.getClass().getSimpleName());
        details.put("supportedMediaTypes", ex.getSupportedMediaTypes());
        
        ApiResponse<String> errorResponse = ApiResponse.error("Unsupported Media Type", "지원하지 않는 Content-Type입니다.", 415);
        errorResponse.setDetails(details);
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        log.warn("Message not readable: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("path", request.getDescription(false));
        details.put("exception", ex.getClass().getSimpleName());
        
        ApiResponse<String> errorResponse = ApiResponse.error("Bad Request", "잘못된 요청 형식입니다.", 400);
        errorResponse.setDetails(details);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> details = new HashMap<>();
        details.put("path", request.getDescription(false));
        details.put("exception", ex.getClass().getSimpleName());
        
        ApiResponse<String> errorResponse = ApiResponse.error("Internal Server Error", "서버 내부 오류가 발생했습니다.", 500);
        errorResponse.setDetails(details);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.warn("Illegal argument exception: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("path", request.getDescription(false));
        details.put("exception", ex.getClass().getSimpleName());
        
        ApiResponse<String> errorResponse = ApiResponse.error("Invalid Argument", ex.getMessage(), 400);
        errorResponse.setDetails(details);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.warn("Runtime exception: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("path", request.getDescription(false));
        details.put("exception", ex.getClass().getSimpleName());
        
        ApiResponse<String> errorResponse = ApiResponse.error("Runtime Error", ex.getMessage(), 400);
        errorResponse.setDetails(details);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomBusinessException(CustomBusinessException ex, WebRequest request) {
        log.warn("Custom business exception: {} - Error Code: {}", ex.getMessage(), ex.getErrorCode());
        
        Map<String, Object> details = new HashMap<>();
        details.put("path", request.getDescription(false));
        details.put("errorCode", ex.getErrorCode());
        details.put("exception", ex.getClass().getSimpleName());
        
        ApiResponse<String> errorResponse = ApiResponse.error("Business Error", ex.getMessage(), ex.getHttpStatus().value());
        errorResponse.setDetails(details);
        
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

} 