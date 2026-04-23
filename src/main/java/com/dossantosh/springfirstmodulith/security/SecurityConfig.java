package com.dossantosh.springfirstmodulith.security;

import com.dossantosh.springfirstmodulith.core.datasource.runtime.DataViewFromSessionFilter;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetailsService;
import com.dossantosh.springfirstmodulith.security.login.JsonUsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

import java.util.List;

@Configuration
public class SecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;

	public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager,
			DataViewFromSessionFilter dataViewFromSessionFilter) throws Exception {

		JsonUsernamePasswordAuthenticationFilter jsonLoginFilter = new JsonUsernamePasswordAuthenticationFilter();
		jsonLoginFilter.setAuthenticationManager(authenticationManager);

		jsonLoginFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {

			Object rawDataSource = request.getAttribute(JsonUsernamePasswordAuthenticationFilter.REQ_ATTR_DATA_SOURCE);
			String dataSource = rawDataSource == null ? "prod" : rawDataSource.toString();

			if (!"historic".equals(dataSource)) {
				dataSource = "prod";
			}

			request.getSession(true).setAttribute(DataViewFromSessionFilter.SESSION_KEY, dataSource);

			response.setStatus(200);
			response.setContentType("application/json");
			response.getWriter().write("{\"username\":\"" + authentication.getName() + "\"}");
		});

		jsonLoginFilter.setAuthenticationFailureHandler((request, response, exception) -> response.setStatus(401));

		return http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).spa())
				.sessionManagement(session -> session

						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
						.sessionFixation(SessionFixationConfigurer::migrateSession))
				.securityContext(securityContext -> securityContext.requireExplicitSave(false))
				.userDetailsService(customUserDetailsService).authorizeHttpRequests(auth -> auth

						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						.requestMatchers("/api/auth/login", "/api/auth/csrf").permitAll()

						.requestMatchers("/api/**").authenticated()

						.anyRequest().authenticated())

				.formLogin(AbstractHttpConfigurer::disable)

				.httpBasic(AbstractHttpConfigurer::disable)

				.logout(logout -> logout.logoutUrl("/api/auth/logout").invalidateHttpSession(true)

						.deleteCookies("JSESSIONID", "SESSION")
						.logoutSuccessHandler((req, res, auth) -> res.setStatus(204)))

				.addFilterBefore(dataViewFromSessionFilter, UsernamePasswordAuthenticationFilter.class)

				.addFilterAt(jsonLoginFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

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

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
