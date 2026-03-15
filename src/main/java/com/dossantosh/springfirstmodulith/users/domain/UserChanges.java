package com.dossantosh.springfirstmodulith.users.domain;

import com.dossantosh.springfirstmodulith.core.errors.custom.BusinessException;

public record UserChanges(
        String username,
        String email,
        Boolean enabled,
        String password,
        Boolean isAdmin,
        UserAccess access) {

    public UserChanges {
        if (access == null) {
            throw new BusinessException("User access cannot be null");
        }
    }
}
