package org.biz.shopverse.exception;

import lombok.extern.slf4j.Slf4j;
import org.biz.shopverse.dto.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse createErrorResponse(HttpStatus status, String error, String message, Map<String, Object> details) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .details(details)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation error occurred: {}", ex.getMessage());
        
        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        // 필드와 무관한 에러 체크
        ex.getBindingResult().getGlobalErrors().forEach(error ->
            errors.put("global", error.getDefaultMessage())
        );

        return ResponseEntity.badRequest()
                .body(createErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", "입력값 검증에 실패했습니다.", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "서버 내부 오류가 발생했습니다.", 
                        Map.of("path", request.getDescription(false))));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.warn("Illegal argument exception: {}", ex.getMessage());
        
        return ResponseEntity.badRequest()
                .body(createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Argument", ex.getMessage(), 
                        Map.of("path", request.getDescription(false))));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.warn("Runtime exception: {}", ex.getMessage());
        
        return ResponseEntity.badRequest()
                .body(createErrorResponse(HttpStatus.BAD_REQUEST, "Runtime Error", ex.getMessage(), 
                        Map.of("path", request.getDescription(false))));
    }

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<ErrorResponse> handleCustomBusinessException(CustomBusinessException ex, WebRequest request) {
        log.warn("Custom business exception: {} - Error Code: {}", ex.getMessage(), ex.getErrorCode());
        
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", ex.getErrorCode());
        details.put("path", request.getDescription(false));
        
        return ResponseEntity.status(ex.getHttpStatus())
                .body(createErrorResponse(ex.getHttpStatus(), "Business Error", ex.getMessage(), details));
    }

} 