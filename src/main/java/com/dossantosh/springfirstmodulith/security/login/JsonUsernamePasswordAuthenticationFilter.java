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
import java.util.Map;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	public static final String REQ_ATTR_DATA_SOURCE = "REQ_DATA_SOURCE";

	private final ObjectMapper objectMapper = new ObjectMapper();

	public JsonUsernamePasswordAuthenticationFilter() {

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
			@SuppressWarnings("unchecked")
			Map<String, Object> body = objectMapper.readValue(request.getInputStream(), Map.class);

			String username = body.getOrDefault("username", "").toString();
			String password = body.getOrDefault("password", "").toString();

			Object rawDataSource = body.containsKey("dataSource") ? body.get("dataSource") : body.getOrDefault("view", "prod");
			String dataSource = rawDataSource == null ? "prod" : rawDataSource.toString();
			request.setAttribute(REQ_ATTR_DATA_SOURCE, dataSource);

			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,
					password);
			setDetails(request, authRequest);
			return this.getAuthenticationManager().authenticate(authRequest);
		} catch (IOException e) {
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("", "");
			setDetails(request, authRequest);
			return this.getAuthenticationManager().authenticate(authRequest);
		}
	}
}
