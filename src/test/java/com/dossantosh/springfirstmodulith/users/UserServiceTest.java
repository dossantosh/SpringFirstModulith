package com.dossantosh.springfirstmodulith.users;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.application.dtos.UserDTO;
import com.dossantosh.springfirstmodulith.users.application.services.ModuleService;
import com.dossantosh.springfirstmodulith.users.application.services.RoleService;
import com.dossantosh.springfirstmodulith.users.application.services.SubmoduleService;
import com.dossantosh.springfirstmodulith.users.application.services.UserService;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private ModuleService moduleService;
    @Mock
    private SubmoduleService submoduleService;

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
        // when(extra.getId()).thenReturn(12L);

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
        // Arrange
        int limit = 2;
        Long lastId = 20L;

        // Simulate repo returning results for PREVIOUS direction (often descending),
        // and the service reverses it to ascending.
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
        // when(extra.getId()).thenReturn(9L);

        when(userRepository.findUsersKeyset(null, null, null, lastId, limit + 1, Direction.PREVIOUS.name()))
                .thenReturn(new ArrayList<>(List.of(p1, p2, extra)));

        // Act
        KeysetPage<UserDTO> page = userService.findUsersKeyset(null, null, null, lastId, limit, Direction.PREVIOUS);

        // Assert: reversed to ascending [10, 11]
        assertThat(page.getContent()).extracting(UserDTO::getId).containsExactly(10L, 11L);

        // Cursor semantics after reverse
        assertThat(page.getPreviousId()).isEqualTo(10L);
        assertThat(page.getNextId()).isEqualTo(11L);

        // For PREVIOUS:
        // hasPrevious = hasMore (true because extra existed)
        // hasNext = lastId != null (true because we came from "middle")
        assertThat(page.isHasPrevious()).isTrue();
        assertThat(page.isHasNext()).isTrue();
    }

    @Test
    void findUsersKeyset_whenNoResults_returnsEmptyPageWithNullCursors() {
        // Arrange
        int limit = 10;

        when(userRepository.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
                .thenReturn(new ArrayList<>());

        // Act
        KeysetPage<UserDTO> page = userService.findUsersKeyset(null, null, null, null, limit, Direction.NEXT);

        // Assert
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getNextId()).isNull();
        assertThat(page.getPreviousId()).isNull();
        assertThat(page.isHasNext()).isFalse();
        assertThat(page.isHasPrevious()).isFalse();
    }

    @Test
    void modifyUser_whenAssociationsProvided_thenSavesMergedIncomingUser() {
        // Arrange
        User existing = new User();
        existing.setId(5L);
        existing.setEmail("old@x.com");
        existing.setEnabled(true);
        existing.setPassword("hashed");

        Roles role = new Roles();
        Modules module = new Modules();
        Submodules submodule = new Submodules();

        User incoming = new User();
        incoming.setEmail("   ");
        incoming.setPassword("");
        incoming.setEnabled(null);

        incoming.setRoles(Set.of(role));
        incoming.setModules(Set.of(module));
        incoming.setSubmodules(Set.of(submodule));

        // Act
        userService.modifyUser(incoming, existing);

        // Assert: capture what was saved
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());

        User saved = captor.getValue();

        // if your service saves the same instance, this will still pass
        assertThat(saved).isSameAs(incoming);

        // preserved/copied from existing
        assertThat(saved.getId()).isEqualTo(5L);
        assertThat(saved.getEmail()).isEqualTo("old@x.com");
        assertThat(saved.getPassword()).isEqualTo("hashed");
        assertThat(saved.getEnabled()).isTrue();

        // associations kept
        assertThat(saved.getRoles()).containsExactly(role);
        assertThat(saved.getModules()).containsExactly(module);
        assertThat(saved.getSubmodules()).containsExactly(submodule);

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void modifyUser_whenAssociationsMissing_thenReturnsEarly_andDoesNotSave() {
        User existing = new User();
        existing.setId(5L);
        existing.setEmail("old@x.com");
        existing.setEnabled(true);
        existing.setPassword("hashed");

        User incoming = new User();
        incoming.setEmail("");
        incoming.setPassword("");
        incoming.setEnabled(null);

        incoming.setRoles(null);
        incoming.setModules(null);
        incoming.setSubmodules(null);

        userService.modifyUser(incoming, existing);

        verifyNoInteractions(userRepository);

        assertThat(incoming.getId()).isNull();
        assertThat(incoming.getEmail()).isEmpty();
        assertThat(incoming.getEnabled()).isNull();
        assertThat(incoming.getPassword()).isEmpty();
    }

    @Test
    void createUser_setsDefaults_andSaves_whenDefaultRoleModuleSubmoduleExist() {
        // given default IDs in your code: 1L, 1L, 1L
        Roles role = new Roles();
        role.setId(1L);
        role.setName("ROLE_USER");

        Modules module = new Modules();
        module.setId(1L);
        module.setName("DEFAULT_MODULE");

        Submodules submodule = new Submodules();
        submodule.setId(1L);
        submodule.setName("DEFAULT_SUBMODULE");

        when(roleService.existById(1L)).thenReturn(true);
        when(roleService.findById(1L)).thenReturn(role);

        when(moduleService.existById(1L)).thenReturn(true);
        when(moduleService.findById(1L)).thenReturn(module);

        when(submoduleService.existById(1L)).thenReturn(true);
        when(submoduleService.findById(1L)).thenReturn(submodule);

        User newUser = new User();
        newUser.setUsername("john");
        newUser.setEmail("john@x.com");

        // when
        userService.createUser(newUser);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getEnabled()).isTrue();
        assertThat(saved.getRoles()).extracting("id").contains(1L);
        assertThat(saved.getModules()).extracting("id").contains(1L);
        assertThat(saved.getSubmodules()).extracting("id").contains(1L);
    }

    @Test
    void createUser_doesNotSave_whenAnyDefaultIsMissing() {
        when(roleService.existById(1L)).thenReturn(true);
        when(roleService.findById(1L)).thenReturn(new Roles());

        when(moduleService.existById(1L)).thenReturn(true);
        when(moduleService.findById(1L)).thenReturn(new Modules());

        when(submoduleService.existById(1L)).thenReturn(false); // missing

        User newUser = new User();
        userService.createUser(newUser);

        verifyNoInteractions(userRepository);
    }

}
