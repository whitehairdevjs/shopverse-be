package org.biz.shopverse.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private String error;
    private int status;
    private String timestamp;
    private Map<String, Object> details;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .status(200)
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .status(200)
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }
    
    public static <T> ApiResponse<T> error(String error, String message, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }
} 