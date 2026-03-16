package com.dossantosh.springfirstmodulith.users.application.services;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.application.views.ModuleView;
import com.dossantosh.springfirstmodulith.users.application.views.RoleView;
import com.dossantosh.springfirstmodulith.users.application.views.SubmoduleView;
import com.dossantosh.springfirstmodulith.users.application.views.UserDetailsView;
import com.dossantosh.springfirstmodulith.users.application.views.UserSummaryView;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.infrastructure.mappers.UserMapper;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserAuthProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserQueryService {

    private final UserRepository userRepository;

    public KeysetPage<UserSummaryView> findUsersKeyset(Long id, String username, String email, Long lastId, int limit,
            Direction direction) {
        List<UserProjection> userProjections = userRepository.findUsersKeyset(id, username, email, lastId, limit + 1,
                direction.name());

        boolean hasMore = userProjections.size() > limit;
        if (hasMore) {
            userProjections.remove(userProjections.size() - 1);
        }

        if (direction == Direction.PREVIOUS) {
            Collections.reverse(userProjections);
        }

        List<UserSummaryView> userSummaries = userProjections.stream()
                .map(this::toUserSummaryView)
                .toList();

        Long newNextId = null;
        Long newPreviousId = null;
        boolean hasNext = false;
        boolean hasPrevious = false;

        if (!userSummaries.isEmpty()) {
            newNextId = userSummaries.get(userSummaries.size() - 1).id();
            newPreviousId = userSummaries.get(0).id();
            if (direction == Direction.NEXT) {
                hasNext = hasMore;
                hasPrevious = lastId != null;
            } else if (direction == Direction.PREVIOUS) {
                hasNext = lastId != null;
                hasPrevious = hasMore;
            }
        }

        KeysetPage<UserSummaryView> page = new KeysetPage<>();
        page.setContent(userSummaries);
        page.setNextId(newNextId);
        page.setPreviousId(newPreviousId);
        page.setHasNext(hasNext);
        page.setHasPrevious(hasPrevious);

        return page;
    }

    public UserDetailsView getUserDetails(Long id) {
        return userRepository.findFullUserById(id)
                .map(UserMapper::toDomain)
                .map(this::toUserDetailsView)
                .orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
    }

    public UserAuthProjection findUserAuthByUsername(String username) {
        return userRepository.findUserAuthByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::toDomain)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toDomain)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserSummaryView toUserSummaryView(UserProjection userProjection) {
        if (userProjection == null) {
            return null;
        }

        return new UserSummaryView(
                userProjection.getId(),
                userProjection.getUsername(),
                userProjection.getEmail(),
                userProjection.getEnabled(),
                userProjection.getIsAdmin());
    }

    private UserDetailsView toUserDetailsView(User user) {
        if (user == null) {
            return null;
        }

        LinkedHashSet<RoleView> roleViews = new LinkedHashSet<>();
        for (Roles role : user.getRoles()) {
            roleViews.add(new RoleView(role.getId(), role.getName()));
        }

        LinkedHashSet<ModuleView> moduleViews = new LinkedHashSet<>();
        for (Modules module : user.getModules()) {
            moduleViews.add(new ModuleView(module.getId(), module.getName()));
        }

        LinkedHashSet<SubmoduleView> submoduleViews = new LinkedHashSet<>();
        for (Submodules submodule : user.getSubmodules()) {
            submoduleViews.add(new SubmoduleView(submodule.getId(), submodule.getName()));
        }

        return new UserDetailsView(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getEnabled(),
                user.getIsAdmin(),
                roleViews,
                moduleViews,
                submoduleViews);
    }
}
