package com.dossantosh.springfirstmodulith.security.api;

import jakarta.servlet.http.HttpSession;
import com.dossantosh.springfirstmodulith.security.AuthorizationService;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetails;
import com.dossantosh.springfirstmodulith.security.session.CurrentDataViewQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final CurrentDataViewQuery currentDataViewQuery;
	private final AuthorizationService authorizationService;

	public AuthController(CurrentDataViewQuery currentDataViewQuery, AuthorizationService authorizationService) {
		this.currentDataViewQuery = currentDataViewQuery;
		this.authorizationService = authorizationService;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/me")
	public ResponseEntity<?> me(Authentication authentication, HttpSession session) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(401).build();
		}

		String dataSource = currentDataViewQuery.getCurrentDataView(session);

		return ResponseEntity.ok(sessionResponse(authentication, dataSource));
	}

	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken token) {
		return token;
	}

	private AuthSessionResponse sessionResponse(Authentication authentication, String dataSource) {
		List<String> scopes = authorizationService.effectiveScopes(authentication);

		return new AuthSessionResponse(userId(authentication), authentication.getName(), dataSource,
				authorizationService.roles(authentication), scopes, authorizationService.capabilities(authentication),
				AuthNavigationMapper.fromScopes(scopes));
	}

	private Long userId(Authentication authentication) {
		if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
			return userDetails.getId();
		}
		return null;
	}
}
