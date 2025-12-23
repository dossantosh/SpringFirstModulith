package com.dossantosh.springfirstmodulith.security.custom;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthQuery;
import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthView;

/**
 * Loads user authentication data from the users module API.
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAuthQuery userAuthQuery;

    CustomUserDetailsService(UserAuthQuery userAuthQuery) {
        this.userAuthQuery = userAuthQuery;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserAuthView user = userAuthQuery.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        CustomUserDetails userDetails = new CustomUserDetails();

        userDetails.setId(user.id());
        userDetails.setUsername(user.username());
        userDetails.setEmail(user.email());
        userDetails.setPassword(user.password());
        userDetails.setEnabled(user.enabled());
        userDetails.setIsAdmin(user.isAdmin());
        userDetails.setRoles(new java.util.LinkedHashSet<>(user.roles()));
        userDetails.setModules(new java.util.LinkedHashSet<>(user.modules()));
        userDetails.setSubmodules(new java.util.LinkedHashSet<>(user.submodules()));

        return userDetails;
    }
}
