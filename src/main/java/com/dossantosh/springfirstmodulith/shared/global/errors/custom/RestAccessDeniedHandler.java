package com.dossantosh.springfirstmodulith.shared.global.errors.custom;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom AccessDeniedHandler that handles 403 Forbidden errors in REST APIs.
 * 
 * This handler intercepts AccessDeniedException thrown by Spring Security when
 * a user attempts to access a resource they do not have permission for.
 * It returns a JSON response with relevant error details including HTTP status,
 * error message, and the requested path.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public RestAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Handles the AccessDeniedException by setting the HTTP status to 403
     * Forbidden,
     * setting the response content type to JSON, and writing a JSON object with
     * error details to the response output stream.
     *
     * @param request               the HttpServletRequest that resulted in an
     *                              AccessDeniedException
     * @param response              the HttpServletResponse to write the error
     *                              details to
     * @param accessDeniedException the AccessDeniedException that triggered this
     *                              handler
     * @throws IOException      if an input or output exception occurs
     * @throws ServletException if a servlet-specific exception occurs
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
        response.setContentType("application/json");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 403);
        errorDetails.put("error", "Forbidden");
        errorDetails.put("message", accessDeniedException.getMessage());
        errorDetails.put("path", request.getRequestURI());

        mapper.writeValue(response.getOutputStream(), errorDetails);
    }
}
