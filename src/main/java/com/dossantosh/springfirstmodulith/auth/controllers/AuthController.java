package com.dossantosh.springfirstmodulith.auth.controllers;

import com.dossantosh.springfirstmodulith.core.datasource.runtime.DataViewFromSessionFilter;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/me")
	public ResponseEntity<?> me(org.springframework.security.core.Authentication authentication, HttpSession session) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(401).build();
		}

		String dataSource = "prod";
		if (session != null) {
			Object raw = session.getAttribute(DataViewFromSessionFilter.SESSION_KEY);
			if ("historic".equals(raw)) {
				dataSource = "historic";
			}
		}

		return ResponseEntity.ok(new AuthSessionResponse(authentication.getName(),
				authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList(), dataSource));
	}

	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken token) {
		return token;
	}
}
