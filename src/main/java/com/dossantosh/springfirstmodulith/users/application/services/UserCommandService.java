package com.dossantosh.springfirstmodulith.users.application.services;

import org.springframework.stereotype.Service;

import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import com.dossantosh.springfirstmodulith.users.infrastructure.mappers.UserMapper;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCommandService {

    private final UserRepository userRepository;
    private final DefaultUserAccessPolicyService defaultUserAccessPolicyService;

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with ID " + id + " not found");
        }

        userRepository.deleteById(id);
    }

    public void modifyUser(User changes, User existingUser) {
        UserAccess requestedAccess = UserAccess.of(
                toMutableSet(changes.getRoles()),
                toMutableSet(changes.getModules()),
                toMutableSet(changes.getSubmodules()));

        existingUser.applyChangesFrom(changes, requestedAccess);
        save(existingUser);
    }

    public void createUser(User user) {
        user.prepareForCreation();
        if (user.hasNoAccessAssigned()) {
            user.replaceAccess(defaultUserAccessPolicyService.defaultAccessForNewUser());
        }
        save(user);
    }

    private User save(User user) {
        return UserMapper.toDomain(userRepository.save(UserMapper.toJpaEntity(user)));
    }

    private static <T> java.util.Set<T> toMutableSet(java.util.Set<T> values) {
        return values == null ? null : new java.util.LinkedHashSet<>(values);
    }
}
