package com.dossantosh.springfirstmodulith.security.api;

import jakarta.servlet.http.HttpSession;
import com.dossantosh.springfirstmodulith.security.session.CurrentDataViewQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final CurrentDataViewQuery currentDataViewQuery;

	public AuthController(CurrentDataViewQuery currentDataViewQuery) {
		this.currentDataViewQuery = currentDataViewQuery;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/me")
	public ResponseEntity<?> me(org.springframework.security.core.Authentication authentication, HttpSession session) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(401).build();
		}

		String dataSource = currentDataViewQuery.getCurrentDataView(session);
		var authorities = authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList();

		return ResponseEntity.ok(new AuthSessionResponse(authentication.getName(), authorities, dataSource,
				AuthCapabilitiesMapper.fromAuthorities(authorities)));
	}

	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken token) {
		return token;
	}
}
