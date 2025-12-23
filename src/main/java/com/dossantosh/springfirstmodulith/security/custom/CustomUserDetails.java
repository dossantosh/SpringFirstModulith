package com.dossantosh.springfirstmodulith.security.custom;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * Custom implementation of Spring Security's {@link UserDetails}
 * representing authenticated user details including roles and permissions.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Boolean enabled;
    private Boolean isAdmin;
    private LinkedHashSet<Long> roles = new LinkedHashSet<>();
    private LinkedHashSet<Long> modules = new LinkedHashSet<>();
    private LinkedHashSet<Long> submodules = new LinkedHashSet<>();

    /**
     * Returns authorities granted to the user, based on roles.
     * 
     * @return collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("" + role))
                .collect(Collectors.toList());
    }

    /**
     * Indicates whether the user's account has expired.
     * 
     * @return true (account is never expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * 
     * @return true (account is never locked)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     * 
     * @return true (credentials are never expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * 
     * @return true if enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
}
