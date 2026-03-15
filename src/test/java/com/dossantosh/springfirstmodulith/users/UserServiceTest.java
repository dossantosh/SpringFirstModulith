package com.dossantosh.springfirstmodulith.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dossantosh.springfirstmodulith.core.errors.custom.BusinessException;
import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.application.dtos.UserDTO;
import com.dossantosh.springfirstmodulith.users.application.services.DefaultUserAccessPolicyService;
import com.dossantosh.springfirstmodulith.users.application.services.UserService;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.ModuleJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.RoleJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.SubmoduleJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.UserJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DefaultUserAccessPolicyService defaultUserAccessPolicyService;

    @InjectMocks
    private UserService userService;

    @Test
    void findUsersKeyset_next_firstPage_hasMore_setsCursorsAndFlags() {
        int limit = 2;

        UserProjection p1 = mock(UserProjection.class);
        when(p1.getId()).thenReturn(10L);
        when(p1.getUsername()).thenReturn("u1");
        when(p1.getEmail()).thenReturn("e1");
        when(p1.getEnabled()).thenReturn(true);
        when(p1.getIsAdmin()).thenReturn(false);

        UserProjection p2 = mock(UserProjection.class);
        when(p2.getId()).thenReturn(11L);
        when(p2.getUsername()).thenReturn("u2");
        when(p2.getEmail()).thenReturn("e2");
        when(p2.getEnabled()).thenReturn(true);
        when(p2.getIsAdmin()).thenReturn(false);

        UserProjection extra = mock(UserProjection.class);

        when(userRepository.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
                .thenReturn(new ArrayList<>(List.of(p1, p2, extra)));

        KeysetPage<UserDTO> page = userService.findUsersKeyset(null, null, null, null, limit, Direction.NEXT);

        assertThat(page.getContent()).extracting(UserDTO::getId).containsExactly(10L, 11L);
        assertThat(page.getNextId()).isEqualTo(11L);
        assertThat(page.getPreviousId()).isEqualTo(10L);
        assertThat(page.isHasNext()).isTrue();
        assertThat(page.isHasPrevious()).isFalse();
    }

    @Test
    void findUsersKeyset_next_firstPage_noMore_setsHasNextFalse() {
        int limit = 2;

        UserProjection p1 = mock(UserProjection.class);
        when(p1.getId()).thenReturn(10L);
        when(p1.getUsername()).thenReturn("u1");
        when(p1.getEmail()).thenReturn("e1");
        when(p1.getEnabled()).thenReturn(true);
        when(p1.getIsAdmin()).thenReturn(false);

        UserProjection p2 = mock(UserProjection.class);
        when(p2.getId()).thenReturn(11L);
        when(p2.getUsername()).thenReturn("u2");
        when(p2.getEmail()).thenReturn("e2");
        when(p2.getEnabled()).thenReturn(true);
        when(p2.getIsAdmin()).thenReturn(false);

        when(userRepository.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
                .thenReturn(new ArrayList<>(List.of(p1, p2)));

        KeysetPage<UserDTO> page = userService.findUsersKeyset(null, null, null, null, limit, Direction.NEXT);

        assertThat(page.getContent()).extracting(UserDTO::getId).containsExactly(10L, 11L);
        assertThat(page.isHasNext()).isFalse();
        assertThat(page.isHasPrevious()).isFalse();
    }

    @Test
    void findUsersKeyset_previous_hasMore_reversesToAscending_andSetsFlags() {
        int limit = 2;
        Long lastId = 20L;

        UserProjection p1 = mock(UserProjection.class);
        when(p1.getId()).thenReturn(11L);
        when(p1.getUsername()).thenReturn("u2");
        when(p1.getEmail()).thenReturn("u2@x.com");
        when(p1.getEnabled()).thenReturn(true);
        when(p1.getIsAdmin()).thenReturn(false);

        UserProjection p2 = mock(UserProjection.class);
        when(p2.getId()).thenReturn(10L);
        when(p2.getUsername()).thenReturn("u1");
        when(p2.getEmail()).thenReturn("u1@x.com");
        when(p2.getEnabled()).thenReturn(true);
        when(p2.getIsAdmin()).thenReturn(false);

        UserProjection extra = mock(UserProjection.class);

        when(userRepository.findUsersKeyset(null, null, null, lastId, limit + 1, Direction.PREVIOUS.name()))
                .thenReturn(new ArrayList<>(List.of(p1, p2, extra)));

        KeysetPage<UserDTO> page = userService.findUsersKeyset(null, null, null, lastId, limit, Direction.PREVIOUS);

        assertThat(page.getContent()).extracting(UserDTO::getId).containsExactly(10L, 11L);
        assertThat(page.getPreviousId()).isEqualTo(10L);
        assertThat(page.getNextId()).isEqualTo(11L);
        assertThat(page.isHasPrevious()).isTrue();
        assertThat(page.isHasNext()).isTrue();
    }

    @Test
    void findUsersKeyset_whenNoResults_returnsEmptyPageWithNullCursors() {
        int limit = 10;

        when(userRepository.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
                .thenReturn(new ArrayList<>());

        KeysetPage<UserDTO> page = userService.findUsersKeyset(null, null, null, null, limit, Direction.NEXT);

        assertThat(page.getContent()).isEmpty();
        assertThat(page.getNextId()).isNull();
        assertThat(page.getPreviousId()).isNull();
        assertThat(page.isHasNext()).isFalse();
        assertThat(page.isHasPrevious()).isFalse();
    }

    @Test
    void modifyUser_whenValidChangesProvided_updatesExistingAggregateAndSaves() {
        Modules usersModule = module(10L, "Users");
        Roles userRole = role(20L, "USER");
        Submodules readUsers = submodule(30L, "ReadUsers", usersModule);

        User existing = new User("john", "old@x.com", "hashed", false);
        existing.setId(5L);
        existing.replaceAccess(UserAccess.of(Set.of(userRole), Set.of(usersModule), Set.of(readUsers)));

        User incoming = new User();
        incoming.setEmail("new@x.com");
        incoming.setEnabled(false);
        incoming.replaceAccess(UserAccess.of(Set.of(userRole), Set.of(usersModule), Set.of(readUsers)));

        userService.modifyUser(incoming, existing);

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
        User existing = new User("john", "old@x.com", "hashed", false);
        existing.setId(5L);

        User incoming = new User();
        incoming.setEmail("new@x.com");

        assertThatThrownBy(() -> userService.modifyUser(incoming, existing))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("at least one role");

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

        userService.createUser(newUser);

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

        userService.createUser(newUser);

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
