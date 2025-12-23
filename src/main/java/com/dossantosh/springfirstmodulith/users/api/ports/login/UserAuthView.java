package com.dossantosh.springfirstmodulith.users.api.ports.login;

import java.util.List;

/**
 * Immutable view of authentication and authorization data required by security.
 */
public record UserAuthView(
        Long id,
        String username,
        String email,
        String password,
        boolean enabled,
        boolean isAdmin,
        List<Long> roles,
        List<Long> modules,
        List<Long> submodules
) {}