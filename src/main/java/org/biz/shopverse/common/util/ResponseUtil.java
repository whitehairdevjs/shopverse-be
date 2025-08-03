package org.biz.shopverse.common.util;

import org.biz.shopverse.dto.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void writeUnauthorizedError(HttpServletResponse response, String code, String message) throws IOException {
        ErrorResponse error = new ErrorResponse(code, message);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }

}