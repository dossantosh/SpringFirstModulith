package com.dossantosh.springfirstmodulith.core.exceptions.custom;

import com.dossantosh.springfirstmodulith.core.exceptions.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");

		ApiError apiError = new ApiError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
				authException.getMessage(), request.getRequestURI());

		mapper.writeValue(response.getOutputStream(), apiError);
	}
}
