package com.dossantosh.springfirstmodulith.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Objects;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	public static final String REQ_ATTR_DATA_SOURCE = "REQ_DATA_SOURCE";

	private static final String DEFAULT_DATA_SOURCE = "prod";

	private final ObjectMapper objectMapper;

	public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {

		this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper cannot be null");

		setFilterProcessesUrl("/api/auth/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		String contentType = request.getContentType();
		if (contentType == null || !contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
			return super.attemptAuthentication(request, response);
		}

		try {
			LoginRequest body = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
			request.setAttribute(REQ_ATTR_DATA_SOURCE, body.resolvedDataSource());

			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
					body.resolvedUsername(), body.resolvedPassword());
			setDetails(request, authRequest);
			return this.getAuthenticationManager().authenticate(authRequest);
		} catch (IOException e) {
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("", "");
			setDetails(request, authRequest);
			return this.getAuthenticationManager().authenticate(authRequest);
		}
	}

	private record LoginRequest(String username, String password, String dataSource, String view) {

		private String resolvedUsername() {
			return username == null ? "" : username;
		}

		private String resolvedPassword() {
			return password == null ? "" : password;
		}

		private String resolvedDataSource() {
			if (dataSource != null) {
				return dataSource;
			}
			return view == null ? DEFAULT_DATA_SOURCE : view;
		}
	}
}
