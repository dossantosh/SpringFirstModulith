package com.dossantosh.springfirstmodulith.users.application.services;

import java.util.List;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserAuthProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

/**
 * Service class that manages users, their roles, modules, and submodules.
 * Provides CRUD operations, paging, mapping between entities and DTOs,
 * and audit logging for user-related actions.
 */
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final ModuleService moduleService;

    private final SubmoduleService submoduleService;

    /**
     * Retrieves a paged list of users using keyset pagination.
     * 
     * @param id        Filter by user ID.
     * @param username  Filter by username.
     * @param email     Filter by email.
     * @param lastId    The last ID seen in pagination.
     * @param limit     Maximum number of users to return.
     * @param direction Pagination direction (NEXT or PREVIOUS).
     * @return A KeysetPage containing a list of UserDTO and pagination info.
     */
    public KeysetPage<UserDTO> findUsersKeyset(Long id, String username, String email, Long lastId, int limit,
            Direction direction) {
        // Fetch one extra to detect if more elements exist
        List<UserProjection> usersProjections = userRepository.findUsersKeyset(id, username, email, lastId, limit + 1,
                direction.name());

        boolean hasMore = usersProjections.size() > limit;
        if (hasMore) {
            usersProjections.remove(usersProjections.size() - 1); // Remove the extra one
        }

        // Reverse the list if paging backwards to keep ascending order
        if (direction == Direction.PREVIOUS) {
            Collections.reverse(usersProjections);
        }

        List<UserDTO> users = usersProjections.stream()
                .map(this::mapToUserDTO) // <-- mapping here
                .toList();

        Long newNextId = null;
        Long newPreviousId = null;
        boolean hasNext = false;
        boolean hasPrevious = false;

        if (!users.isEmpty()) {
            newNextId = users.get(users.size() - 1).getId(); // Last visible user id for next page
            newPreviousId = users.get(0).getId(); // First visible user id for previous page
            if (direction == Direction.NEXT) {
                hasNext = hasMore;
                hasPrevious = lastId != null;
            } else if (direction == Direction.PREVIOUS) {
                hasNext = lastId != null; // Only has next if came from middle
                hasPrevious = hasMore; // Only has previous if extra found in query
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

    /**
     * Finds a user by their ID.
     * 
     * @param id The user ID.
     * @return User entity.
     * @throws EntityNotFoundException if no user found with the given ID.
     */
    public FullUserDTO getUserDetails(Long id) {

        User user = userRepository.findFullUserById(id).orElse(null);

        if (user == null) {
            return null;
        }
        return mapToFullUserDTO(user);
    }

    /**
     * Retrieves all users from the repository.
     * 
     * @return List of all users.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Finds a user by their ID.
     * 
     * @param id The user ID.
     * @return User entity.
     * @throws EntityNotFoundException if no user found with the given ID.
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
    }

    /**
     * Finds user authentication details by username.
     * 
     * @param username The username.
     * @return UserAuthProjection containing authentication data.
     * @throws UsernameNotFoundException if no user found with the given username.
     */
    public UserAuthProjection findUserAuthByUsername(String username) {
        return userRepository.findUserAuthByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Finds a user by username.
     * 
     * @param username The username.
     * @return User entity.
     * @throws UsernameNotFoundException if no user found with the given username.
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Finds a user by email.
     * 
     * @param email The user's email.
     * @return User entity.
     * @throws UsernameNotFoundException if no user found with the given email.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    /**
     * Checks if a user exists by ID.
     * 
     * @param id User ID.
     * @return true if user exists, false otherwise.
     */
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Checks if a user exists by username.
     * 
     * @param username Username.
     * @return true if user exists, false otherwise.
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Checks if a user exists by email.
     * 
     * @param email User's email.
     * @return true if user exists, false otherwise.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Saves or updates a user entity.
     * 
     * @param user User entity to save.
     * @return The saved User entity.
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Deletes a user by ID with audit logging.
     * 
     * @param id User ID to delete.
     * @throws EntityNotFoundException if user does not exist.
     */
    public void deleteById(Long id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException("User with ID " + id + " not found");
        }

        userRepository.deleteById(id);
    }

    /**
     * Modifies an existing user with new data, preserving existing values if null
     * or empty.
     * Performs audit logging after modification.
     * 
     * @param user         New user data.
     * @param existingUser Existing user to update.
     */
    public void modifyUser(User user, User existingUser) {

        if (user.getRoles() == null || user.getRoles().isEmpty()
                || user.getModules() == null || user.getModules().isEmpty()
                || user.getSubmodules() == null || user.getSubmodules().isEmpty()) {
            return;
        }

        if (user.getId() == null || user.getId().toString().isEmpty()) {
            user.setId(existingUser.getId());
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(existingUser.getEmail());
        }

        if (user.getEnabled() == null) {
            user.setEnabled(existingUser.getEnabled());
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        }

        saveUser(user);
    }

    /**
     * Creates a new user with assigned roles, modules, and submodules based on
     * username.
     * Default roles and modules are assigned as well.
     * Performs audit logging after creation.
     * 
     * @param user User entity to create.
     */
    public void createUser(User user) {

        Set<Roles> roles = new HashSet<>();
        Set<Modules> modules = new HashSet<>();
        Set<Submodules> submodules = new HashSet<>();

        Set<Long> rolesId = new HashSet<>();
        Set<Long> modulesId = new HashSet<>();
        Set<Long> submodulesId = new HashSet<>();

        // Add default role/module/submodule IDs
        rolesId.add(1L);
        modulesId.add(1L);
        submodulesId.add(1L);

        // Resolve roles by IDs
        Roles role = null;
        for (Long rol : rolesId) {
            if (roleService.existById(rol)) {
                role = roleService.findById(rol);
                roles.add(role);
            }
        }
        // Resolve modules by IDs
        Modules module = null;
        for (Long moduleId : modulesId) {
            if (moduleService.existById(moduleId)) {
                module = moduleService.findById(moduleId);
                modules.add(module);
            }
        }
        // Resolve submodules by IDs
        Submodules submodule = null;
        for (Long submoduleId : submodulesId) {
            if (submoduleService.existById(submoduleId)) {
                submodule = submoduleService.findById(submoduleId);
                submodules.add(submodule);
            }
        }

        if (roles.isEmpty() || modules.isEmpty() || submodules.isEmpty()) {
            return;
        }

        user.setEnabled(true);
        user.setRoles(roles);
        user.setModules(modules);
        user.setSubmodules(submodules);

        saveUser(user);
    }

    /**
     * Maps a UserProjection object to a User data transfer object.
     * 
     * @param userProjection The UserProjection object to map.
     * @return UserDTO or null if the input is null.
     */
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

    /**
     * Maps a User entity to a FullUserDTO data transfer object.
     * 
     * @param user User entity.
     * @return FullUserDTO or null if user is null.
     */
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
}
