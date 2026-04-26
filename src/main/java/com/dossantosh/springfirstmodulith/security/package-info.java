/**
 * Technical security module.
 *
 * Owns Spring Security configuration plus the session-backed HTTP endpoints
 * under {@code /api/auth/*}.
 */
@ApplicationModule(allowedDependencies = {"authorization", "users::apiLogin", "core::runtime"})
package com.dossantosh.springfirstmodulith.security;

import org.springframework.modulith.ApplicationModule;
