package com.dossantosh.springfirstmodulith.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * NOTE: Login and logout are handled by Spring Security filters.
     *
     * <p>
     * Why: This guarantees session fixation protection and correct SecurityContext persistence.
     * </p>
     */

    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(Map.of("username", principal.getName()));
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        // returning it forces token creation; CookieCsrfTokenRepository will also write XSRF-TOKEN cookie
        return token;
    }
}
