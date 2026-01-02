package com.dossantosh.springfirstmodulith.core.errors.custom;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.dossantosh.springfirstmodulith.core.errors.ApiError;

import java.io.IOException;

/**
 * Custom AuthenticationEntryPoint that handles 401 Unauthorized errors in REST
 * APIs.
 * 
 * This entry point is triggered when an unauthenticated user tries to access a
 * secured resource. It returns a JSON response containing error details such as
 * HTTP status, error message, and the request path.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Commences an authentication scheme by sending a 401 Unauthorized JSON
     * response.
     *
     * @param request       the HttpServletRequest that resulted in an
     *                      AuthenticationException
     * @param response      the HttpServletResponse to write the error details to
     * @param authException the AuthenticationException that triggered this entry
     *                      point
     * @throws IOException      if an input or output exception occurs
     * @throws ServletException if a servlet-specific exception occurs
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ApiError apiError = new ApiError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized",
                authException.getMessage(),
                request.getRequestURI());

        mapper.writeValue(response.getOutputStream(), apiError);
    }
}
