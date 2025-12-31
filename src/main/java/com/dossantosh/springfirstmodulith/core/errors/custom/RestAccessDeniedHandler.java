package com.dossantosh.springfirstmodulith.core.errors.custom;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.dossantosh.springfirstmodulith.core.errors.ApiError;

import java.io.IOException;
/**
 * Custom AccessDeniedHandler that handles 403 Forbidden errors in REST APIs.
 * 
 * This handler intercepts AccessDeniedException thrown by Spring Security when
 * a user attempts to access a resource they do not have permission for.
 * It returns a JSON response with relevant error details including HTTP status,
 * error message, and the requested path.
 */
@RequiredArgsConstructor
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

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
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ApiError apiError = new ApiError(
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                accessDeniedException.getMessage(),
                request.getRequestURI());

        mapper.writeValue(response.getOutputStream(), apiError);
    }
}
