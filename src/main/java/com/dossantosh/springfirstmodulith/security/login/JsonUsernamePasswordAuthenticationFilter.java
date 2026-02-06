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

/**
 * JSON-based authentication filter for SPA login.
 *
 * <p>
 * Replaces controller-based login so Spring Security can apply:
 * </p>
 * <ul>
 *   <li>Session fixation protection ({@code migrateSession})</li>
 *   <li>SecurityContext persistence to the HTTP session</li>
 * </ul>
 */
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * Request attribute used to pass the selected data view (prod/historic)
     * from {@link #attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     * to the AuthenticationSuccessHandler configured in {@code SecurityConfig}.
     */
    public static final String REQ_ATTR_DATA_VIEW = "REQ_DATA_VIEW";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonUsernamePasswordAuthenticationFilter() {
        // We authenticate on this endpoint.
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

            // Optional: the SPA can send {"view":"prod"|"historic"}.
            // We store it on the request so the success handler can persist it into the session.
            String view = body.getOrDefault("view", "prod").toString();
            request.setAttribute(REQ_ATTR_DATA_VIEW, view);

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken("", "");
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }
}
