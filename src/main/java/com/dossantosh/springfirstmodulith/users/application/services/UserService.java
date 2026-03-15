package com.dossantosh.springfirstmodulith.users.application.services;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.application.dtos.FullUserDTO;
import com.dossantosh.springfirstmodulith.users.application.dtos.UserDTO;
import com.dossantosh.springfirstmodulith.users.application.dtos.roles.ModulesDTO;
import com.dossantosh.springfirstmodulith.users.application.dtos.roles.RolesDTO;
import com.dossantosh.springfirstmodulith.users.application.dtos.roles.SubmodulesDTO;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import com.dossantosh.springfirstmodulith.users.infrastructure.mappers.UserMapper;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserAuthProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final DefaultUserAccessPolicyService defaultUserAccessPolicyService;

    public KeysetPage<UserDTO> findUsersKeyset(Long id, String username, String email, Long lastId, int limit,
            Direction direction) {
        List<UserProjection> usersProjections = userRepository.findUsersKeyset(id, username, email, lastId, limit + 1,
                direction.name());

        boolean hasMore = usersProjections.size() > limit;
        if (hasMore) {
            usersProjections.remove(usersProjections.size() - 1);
        }

        if (direction == Direction.PREVIOUS) {
            Collections.reverse(usersProjections);
        }

        List<UserDTO> users = usersProjections.stream()
                .map(this::mapToUserDTO)
                .toList();

        Long newNextId = null;
        Long newPreviousId = null;
        boolean hasNext = false;
        boolean hasPrevious = false;

        if (!users.isEmpty()) {
            newNextId = users.get(users.size() - 1).getId();
            newPreviousId = users.get(0).getId();
            if (direction == Direction.NEXT) {
                hasNext = hasMore;
                hasPrevious = lastId != null;
            } else if (direction == Direction.PREVIOUS) {
                hasNext = lastId != null;
                hasPrevious = hasMore;
            }
        }

        KeysetPage<UserDTO> page = new KeysetPage<>();
        page.setContent(users);
        page.setNextId(newNextId);
        page.setPreviousId(newPreviousId);
        page.setHasNext(hasNext);
        page.setHasPrevious(hasPrevious);

        return page;
    }

    public FullUserDTO getUserDetails(Long id) {
        return userRepository.findFullUserById(id)
                .map(UserMapper::toDomain)
                .map(this::mapToFullUserDTO)
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

    public User saveUser(User user) {
        return UserMapper.toDomain(userRepository.save(UserMapper.toJpaEntity(user)));
    }

    public void deleteById(Long id) {
        if (!existsById(id)) {
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
        saveUser(existingUser);
    }

    public void createUser(User user) {
        user.prepareForCreation();
        if (user.hasNoAccessAssigned()) {
            user.replaceAccess(defaultUserAccessPolicyService.defaultAccessForNewUser());
        }
        saveUser(user);
    }

    public UserDTO mapToUserDTO(UserProjection userProjection) {
        if (userProjection == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(userProjection.getId());
        dto.setUsername(userProjection.getUsername());
        dto.setEmail(userProjection.getEmail());
        dto.setEnabled(userProjection.getEnabled());
        dto.setIsAdmin(userProjection.getIsAdmin());

        return dto;
    }

    public FullUserDTO mapToFullUserDTO(User user) {
        if (user == null) {
            return null;
        }

        FullUserDTO fullUserDTO = new FullUserDTO();
        fullUserDTO.setId(user.getId());
        fullUserDTO.setUsername(user.getUsername());
        fullUserDTO.setEmail(user.getEmail());
        fullUserDTO.setEnabled(user.getEnabled());
        fullUserDTO.setIsAdmin(user.getIsAdmin());

        LinkedHashSet<RolesDTO> rolesDTOs = new LinkedHashSet<>();
        for (Roles role : user.getRoles()) {
            rolesDTOs.add(new RolesDTO(role.getId(), role.getName()));
        }
        fullUserDTO.setRoles(rolesDTOs);

        LinkedHashSet<ModulesDTO> modulesDTOs = new LinkedHashSet<>();
        for (Modules module : user.getModules()) {
            modulesDTOs.add(new ModulesDTO(module.getId(), module.getName()));
        }
        fullUserDTO.setModules(modulesDTOs);

        LinkedHashSet<SubmodulesDTO> submodulesDTOs = new LinkedHashSet<>();
        for (Submodules submodule : user.getSubmodules()) {
            submodulesDTOs.add(new SubmodulesDTO(submodule.getId(), submodule.getName()));
        }
        fullUserDTO.setSubmodules(submodulesDTOs);

        return fullUserDTO;
    }

    private static <T> java.util.Set<T> toMutableSet(java.util.Set<T> values) {
        return values == null ? null : new java.util.LinkedHashSet<>(values);
    }
}
