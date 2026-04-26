package com.dossantosh.springfirstmodulith.security.api;

import com.dossantosh.springfirstmodulith.security.AuthorizationService;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetails;
import com.dossantosh.springfirstmodulith.security.session.CurrentDataViewQuery;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class CurrentUserController {

	private final CurrentDataViewQuery currentDataViewQuery;
	private final AuthorizationService authorizationService;

	public CurrentUserController(CurrentDataViewQuery currentDataViewQuery, AuthorizationService authorizationService) {
		this.currentDataViewQuery = currentDataViewQuery;
		this.authorizationService = authorizationService;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/capabilities")
	public ResponseEntity<?> capabilities(Authentication authentication, HttpSession session) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(401).build();
		}

		String dataSource = currentDataViewQuery.getCurrentDataView(session);
		return ResponseEntity.ok(new AuthSessionResponse(userId(authentication), authentication.getName(), dataSource,
				authorizationService.roles(authentication), authorizationService.effectiveScopes(authentication),
				authorizationService.capabilities(authentication)));
	}

	private Long userId(Authentication authentication) {
		if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
			return userDetails.getId();
		}
		return null;
	}
}
