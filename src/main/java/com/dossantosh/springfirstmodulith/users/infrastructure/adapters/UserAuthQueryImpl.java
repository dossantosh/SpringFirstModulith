package com.dossantosh.springfirstmodulith.users.infrastructure.adapters;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthQuery;
import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthView;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserAuthProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;

/**
 * Default implementation of {@link UserAuthQuery} backed by {@link UserRepository}.
 */
@Component
class UserAuthQueryImpl implements UserAuthQuery {

    private final UserRepository userRepository;

    UserAuthQueryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserAuthView> findByUsername(String username) {
        return userRepository.findUserAuthByUsername(username)
                .map(this::toView);
    }

    private UserAuthView toView(UserAuthProjection p) {
        return new UserAuthView(
                p.getId(),
                p.getUsername(),
                p.getEmail(),
                p.getPassword(),
                Boolean.TRUE.equals(p.getEnabled()),
                Boolean.TRUE.equals(p.getIsAdmin()),
                nullSafe(p.getRoles()),
                nullSafe(p.getModules()),
                nullSafe(p.getSubmodules())
        );
    }

    private List<Long> nullSafe(List<Long> value) {
        return value == null ? List.of() : value;
    }
}
