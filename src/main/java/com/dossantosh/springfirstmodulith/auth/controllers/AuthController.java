package com.dossantosh.springfirstmodulith.auth.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * NOTE: Login and logout are handled by Spring Security filters.
     *
     * <p>
     * Why: This guarantees session fixation protection and correct SecurityContext
     * persistence.
     * </p>
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<?> me(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList()));
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        // returning it forces token creation; CookieCsrfTokenRepository will also write
        // XSRF-TOKEN cookie
        return token;
    }
}
