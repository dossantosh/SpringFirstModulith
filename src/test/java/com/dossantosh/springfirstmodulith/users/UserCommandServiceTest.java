package com.dossantosh.springfirstmodulith.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dossantosh.springfirstmodulith.core.errors.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.application.services.DefaultUserAccessPolicyService;
import com.dossantosh.springfirstmodulith.users.application.services.UserCommandService;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.domain.UserChanges;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.ModuleJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.RoleJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.SubmoduleJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.UserJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DefaultUserAccessPolicyService defaultUserAccessPolicyService;

    @InjectMocks
    private UserCommandService userCommandService;

    @Test
    void modifyUser_whenValidChangesProvided_updatesExistingAggregateAndSaves() {
        Modules usersModule = module(10L, "Users");
        Roles userRole = role(20L, "USER");
        Submodules readUsers = submodule(30L, "ReadUsers", usersModule);
        UserAccess access = UserAccess.of(Set.of(userRole), Set.of(usersModule), Set.of(readUsers));

        User existing = User.rehydrate(5L, "john", "old@x.com", true, "hashed", false, access);
        UserChanges incoming = new UserChanges(null, "new@x.com", false, null, null, access);

        userCommandService.modifyUser(incoming, existing);

        ArgumentCaptor<UserJpaEntity> captor = ArgumentCaptor.forClass(UserJpaEntity.class);
        verify(userRepository, times(1)).save(captor.capture());

        UserJpaEntity saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(5L);
        assertThat(saved.getEmail()).isEqualTo("new@x.com");
        assertThat(saved.getPassword()).isEqualTo("hashed");
        assertThat(saved.getEnabled()).isFalse();
        assertThat(saved.getRoles()).extracting(RoleJpaEntity::getId, RoleJpaEntity::getName)
                .containsExactly(tuple(20L, "USER"));
        assertThat(saved.getModules()).extracting(ModuleJpaEntity::getId, ModuleJpaEntity::getName)
                .containsExactly(tuple(10L, "Users"));
        assertThat(saved.getSubmodules()).extracting(SubmoduleJpaEntity::getId, SubmoduleJpaEntity::getName)
                .containsExactly(tuple(30L, "ReadUsers"));
    }

    @Test
    void modifyUser_whenAccessIsMissing_throwsBusinessException_andDoesNotSave() {
        User existing = User.rehydrate(5L, "john", "old@x.com", true, "hashed", false,
                UserAccess.of(Set.of(role(20L, "USER")), Set.of(module(10L, "Users")),
                        Set.of(submodule(30L, "ReadUsers", module(10L, "Users")))));

        assertThatThrownBy(() -> userCommandService.modifyUser(
                new UserChanges(null, "new@x.com", null, null, null, null), existing))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User access cannot be null");

        verifyNoInteractions(userRepository);
    }

    @Test
    void createUser_assignsDefaultAccessFromPolicy_andSaves() {
        Modules usersModule = module(1L, "Users");
        Roles userRole = role(2L, "USER");
        Submodules readUsers = submodule(3L, "ReadUsers", usersModule);

        when(defaultUserAccessPolicyService.defaultAccessForNewUser())
                .thenReturn(UserAccess.of(Set.of(userRole), Set.of(usersModule), Set.of(readUsers)));

        User newUser = new User("john", "john@x.com", "secret", false);

        userCommandService.createUser(newUser);

        ArgumentCaptor<UserJpaEntity> captor = ArgumentCaptor.forClass(UserJpaEntity.class);
        verify(userRepository).save(captor.capture());

        UserJpaEntity saved = captor.getValue();
        assertThat(saved.getEnabled()).isTrue();
        assertThat(saved.getRoles()).extracting(RoleJpaEntity::getId, RoleJpaEntity::getName)
                .containsExactly(tuple(2L, "USER"));
        assertThat(saved.getModules()).extracting(ModuleJpaEntity::getId, ModuleJpaEntity::getName)
                .containsExactly(tuple(1L, "Users"));
        assertThat(saved.getSubmodules()).extracting(SubmoduleJpaEntity::getId, SubmoduleJpaEntity::getName)
                .containsExactly(tuple(3L, "ReadUsers"));
    }

    @Test
    void createUser_whenExplicitAccessIsProvided_preservesItAndSkipsDefaultPolicy() {
        Modules adminModule = module(10L, "Admin");
        Roles adminRole = role(20L, "ADMIN");
        Submodules manageUsers = submodule(30L, "ManageUsers", adminModule);

        User newUser = new User("john", "john@x.com", "secret", true);
        newUser.replaceAccess(UserAccess.of(Set.of(adminRole), Set.of(adminModule), Set.of(manageUsers)));

        userCommandService.createUser(newUser);

        ArgumentCaptor<UserJpaEntity> captor = ArgumentCaptor.forClass(UserJpaEntity.class);
        verify(userRepository).save(captor.capture());
        verifyNoInteractions(defaultUserAccessPolicyService);

        UserJpaEntity saved = captor.getValue();
        assertThat(saved.getEnabled()).isTrue();
        assertThat(saved.getIsAdmin()).isTrue();
        assertThat(saved.getRoles()).extracting(RoleJpaEntity::getId, RoleJpaEntity::getName)
                .containsExactly(tuple(20L, "ADMIN"));
        assertThat(saved.getModules()).extracting(ModuleJpaEntity::getId, ModuleJpaEntity::getName)
                .containsExactly(tuple(10L, "Admin"));
        assertThat(saved.getSubmodules()).extracting(SubmoduleJpaEntity::getId, SubmoduleJpaEntity::getName)
                .containsExactly(tuple(30L, "ManageUsers"));
    }

    private static Roles role(Long id, String name) {
        Roles role = new Roles(name);
        role.setId(id);
        return role;
    }

    private static org.assertj.core.groups.Tuple tuple(Object... values) {
        return org.assertj.core.groups.Tuple.tuple(values);
    }

    private static Modules module(Long id, String name) {
        Modules module = new Modules(name);
        module.setId(id);
        return module;
    }

    private static Submodules submodule(Long id, String name, Modules module) {
        Submodules submodule = new Submodules(name, module);
        submodule.setId(id);
        return submodule;
    }
}
