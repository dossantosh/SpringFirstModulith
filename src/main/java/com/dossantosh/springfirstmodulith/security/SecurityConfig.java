package com.dossantosh.springfirstmodulith.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer.SessionFixationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.dossantosh.springfirstmodulith.core.datasource.runtime.DataViewFromSessionFilter;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetailsService;
import com.dossantosh.springfirstmodulith.security.login.JsonUsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Spring Security configuration for a stateful, session-based backend
 * (Spring Session JDBC) consumed by an Angular SPA.
 *
 * <p>
 * Key characteristics:
 * </p>
 * <ul>
 * <li>Uses server-side HTTP sessions (JSESSIONID / Spring Session)</li>
 * <li>Enables CSRF protection using the Angular-friendly cookie/header
 * pattern</li>
 * <li>Allows CORS with credentials so the browser can send session cookies</li>
 * </ul>
 */
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Configures the security filter chain for session-based authentication.
     *
     * <p>
     * Angular SPA expectations:
     * </p>
     * <ul>
     * <li>Browser sends cookies: {@code withCredentials: true}</li>
     * <li>CSRF token cookie {@code XSRF-TOKEN} is readable by JS and sent as header
     * {@code X-XSRF-TOKEN}</li>
     * </ul>
     *
     * @param http Spring Security HTTP builder
     * @return configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager,
            DataViewFromSessionFilter dataViewFromSessionFilter) throws Exception {

        JsonUsernamePasswordAuthenticationFilter jsonLoginFilter = new JsonUsernamePasswordAuthenticationFilter();
        jsonLoginFilter.setAuthenticationManager(authenticationManager);

        jsonLoginFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            // A successful authentication will result in a session being created
            // (IF_REQUIRED)
            // and the SecurityContext being persisted to Spring Session.

            // Persist the selected data view (prod/historic) into the session.
            // The JsonUsernamePasswordAuthenticationFilter stores it as a request
            // attribute.
            Object rawView = request.getAttribute(JsonUsernamePasswordAuthenticationFilter.REQ_ATTR_DATA_VIEW);
            String view = rawView == null ? "prod" : rawView.toString();

            // Only allow the two supported values. Default to prod.
            if (!"historic".equals(view)) {
                view = "prod";
            }

            // Lock the selected view for the whole session.
            request.getSession(true).setAttribute(DataViewFromSessionFilter.SESSION_KEY, view);

            response.setStatus(200);
            response.setContentType("application/json");
            response.getWriter().write("{\"username\":\"" + authentication.getName() + "\"}");
        });

        jsonLoginFilter.setAuthenticationFailureHandler((request, response, exception) -> response.setStatus(401));

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .spa())
                .sessionManagement(session -> session
                        /**
                         * Stateful sessions (required for Spring Session JDBC).
                         * IF_REQUIRED creates a session when needed (e.g., after successful login).
                         */
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(SessionFixationConfigurer::migrateSession))
                .securityContext(securityContext -> securityContext.requireExplicitSave(false))
                .userDetailsService(customUserDetailsService)
                .authorizeHttpRequests(auth -> auth
                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints
                        .requestMatchers("/api/auth/login", "/api/auth/csrf").permitAll()

                        // Everything else requires authentication
                        .requestMatchers("/api/**").authenticated()

                        .anyRequest().authenticated())
                /**
                 * For SPA + JSON login, keep formLogin disabled.
                 *
                 * <p>
                 * Authentication is performed by
                 * {@link JsonUsernamePasswordAuthenticationFilter}
                 * so Spring Security can apply session fixation protection and persist the
                 * SecurityContext into the session.
                 * </p>
                 */
                .formLogin(form -> form.disable())

                .httpBasic(httpBasic -> httpBasic.disable())

                // Proper server-side logout for session apps:
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        // Spring Session can use "SESSION" cookie depending on config.
                        // Keeping both here avoids "sticky" sessions on the client.
                        .deleteCookies("JSESSIONID", "SESSION")
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(204)))

                // Set DataViewContext (prod/historic) for each request
                .addFilterBefore(dataViewFromSessionFilter, UsernamePasswordAuthenticationFilter.class)
                // JSON login filter (POST /api/auth/login)
                .addFilterAt(jsonLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * CORS configuration for an Angular SPA calling this backend using cookies.
     *
     * <p>
     * When using sessions, {@code allowCredentials(true)} is required
     * and you must set explicit allowed origins (no "*").
     * </p>
     *
     * @return configured {@link CorsConfigurationSource}
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Set-Cookie"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DataViewFromSessionFilter dataViewFromSessionFilter() {
        return new DataViewFromSessionFilter();
    }

    /**
     * Exposes the {@link AuthenticationManager} from Spring's
     * {@link AuthenticationConfiguration}.
     *
     * @param config Spring's authentication configuration
     * @return authentication manager
     * @throws Exception if retrieval fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Password encoder used for hashing and verifying user passwords.
     *
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
