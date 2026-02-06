package com.dossantosh.springfirstmodulith.security.login;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;

import com.dossantosh.springfirstmodulith.users.api.ports.login.*;

import java.util.ArrayList;
import java.util.List;

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

        var authorities = new ArrayList<SimpleGrantedAuthority>();


        for (String role : user.roles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + normalize(role)));
        }

        for (String module : user.modules()) {
            authorities.add(new SimpleGrantedAuthority("MODULE_" + normalize(module)));
        }

        for (String sub : user.submodules()) {
            authorities.add(new SimpleGrantedAuthority("SUBMODULE_" + normalize(sub)));
        }

        
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setId(user.id());
        userDetails.setUsername(user.username());
        userDetails.setEmail(user.email());
        userDetails.setPassword(user.password());
        userDetails.setEnabled(user.enabled());
        userDetails.setIsAdmin(user.isAdmin());
        userDetails.setAuthorities(List.copyOf(authorities));

        return userDetails;
    }

    private static String normalize(String value) {
        // "User Management" -> "USER_MANAGEMENT"
        return value == null ? "" : value.trim().toUpperCase().replace(' ', '_');
    }
}
