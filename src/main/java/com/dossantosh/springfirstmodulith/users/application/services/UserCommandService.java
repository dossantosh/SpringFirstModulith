package com.dossantosh.springfirstmodulith.users.application.services;

import org.springframework.stereotype.Service;

import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.domain.UserChanges;
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

    public void modifyUser(UserChanges changes, User existingUser) {
        existingUser.applyChangesFrom(changes);
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
}
