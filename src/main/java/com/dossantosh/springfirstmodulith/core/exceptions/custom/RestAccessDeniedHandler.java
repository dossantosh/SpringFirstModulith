package com.dossantosh.springfirstmodulith.core.exceptions.custom;

import com.dossantosh.springfirstmodulith.core.exceptions.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper mapper;

	public RestAccessDeniedHandler(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json");

		ApiError apiError = new ApiError(HttpServletResponse.SC_FORBIDDEN, "Forbidden",
				accessDeniedException.getMessage(), request.getRequestURI());

		mapper.writeValue(response.getOutputStream(), apiError);
	}
}
