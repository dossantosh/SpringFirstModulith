package com.dossantosh.springfirstmodulith.users.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.dossantosh.springfirstmodulith.core.errors.custom.BusinessException;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class User {

    private Long id;

    private String username;

    private String email;

    private Boolean enabled;

    private String password;

    private Boolean isAdmin;

    private Set<Roles> roles = new HashSet<>();

    private Set<Modules> modules = new HashSet<>();

    private Set<Submodules> submodules = new HashSet<>();

    public User(String username, String email, String password, boolean isAdmin) {
        renameTo(username);
        changeEmail(email);
        changePassword(password);
        setAdmin(isAdmin);
        activate();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = normalizeNullable(username);
    }

    public void setEmail(String email) {
        this.email = normalizeEmailNullable(email);
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setPassword(String password) {
        this.password = normalizeNullable(password);
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Set<Roles> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public Set<Modules> getModules() {
        return Collections.unmodifiableSet(modules);
    }

    public Set<Submodules> getSubmodules() {
        return Collections.unmodifiableSet(submodules);
    }

    public void renameTo(String username) {
        String normalized = normalizeRequired(username, "username");
        this.username = normalized;
    }

    public void changeEmail(String email) {
        String normalized = normalizeEmailRequired(email);
        this.email = normalized;
    }

    public void changePassword(String password) {
        String normalized = normalizeRequired(password, "password");
        this.password = normalized;
    }

    public void activate() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    public void prepareForCreation() {
        renameTo(username);
        changeEmail(email);
        changePassword(password);
        setAdmin(Boolean.TRUE.equals(isAdmin));
        activate();
        validateAccessStateForCreation();
    }

    public void replaceAccess(UserAccess access) {
        if (access == null) {
            throw new BusinessException("User access cannot be null");
        }

        this.roles.clear();
        this.roles.addAll(access.roles());

        this.modules.clear();
        this.modules.addAll(access.modules());

        this.submodules.clear();
        this.submodules.addAll(access.submodules());
    }

    public void applyChangesFrom(User changes, UserAccess requestedAccess) {
        if (changes == null) {
            throw new BusinessException("Changes cannot be null");
        }

        if (hasText(changes.username)) {
            renameTo(changes.username);
        }

        if (hasText(changes.email)) {
            changeEmail(changes.email);
        }

        if (changes.enabled != null) {
            if (Boolean.TRUE.equals(changes.enabled)) {
                activate();
            } else {
                disable();
            }
        }

        if (hasText(changes.password)) {
            changePassword(changes.password);
        }

        if (changes.isAdmin != null) {
            setAdmin(changes.isAdmin);
        }

        replaceAccess(requestedAccess);
    }

    public boolean hasNoAccessAssigned() {
        return roles.isEmpty() && modules.isEmpty() && submodules.isEmpty();
    }

    private static String normalizeRequired(String value, String fieldName) {
        String normalized = normalizeNullable(value);
        if (!hasText(normalized)) {
            throw new BusinessException(fieldName + " cannot be blank");
        }
        return normalized;
    }

    private static String normalizeEmailRequired(String email) {
        String normalized = normalizeEmailNullable(email);
        if (!hasText(normalized)) {
            throw new BusinessException("email cannot be blank");
        }
        return normalized;
    }

    private static String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normalizeEmailNullable(String email) {
        String normalized = normalizeNullable(email);
        return normalized == null ? null : normalized.toLowerCase();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private void validateAccessStateForCreation() {
        boolean hasAnyAccess = !roles.isEmpty() || !modules.isEmpty() || !submodules.isEmpty();
        boolean hasCompleteAccess = !roles.isEmpty() && !modules.isEmpty() && !submodules.isEmpty();

        if (hasAnyAccess && !hasCompleteAccess) {
            throw new BusinessException("User access must be fully specified or omitted during creation");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
