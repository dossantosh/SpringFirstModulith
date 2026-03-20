package com.dossantosh.springfirstmodulith.users;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserCommandPort;
import com.dossantosh.springfirstmodulith.users.application.services.DefaultUserAccessPolicyService;
import com.dossantosh.springfirstmodulith.users.application.services.UserCommandService;
import com.dossantosh.springfirstmodulith.users.domain.*;
import com.dossantosh.springfirstmodulith.users.domain.ports.UserUniquenessPolicy;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

	@Mock
	private UserCommandPort userCommandPort;

	@Mock
	private DefaultUserAccessPolicyService defaultUserAccessPolicyService;

	@Mock
	private UserUniquenessPolicy userUniquenessPolicy;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserCommandService userCommandService;

	@Test
	void modifyUser_whenValidChangesProvided_updatesExistingAggregateAndSaves() {
		Modules usersModule = module(10L, "Users");
		Roles userRole = role(20L, "USER");
		Submodules readUsers = submodule(30L, "ReadUsers", usersModule);
		UserAccess access = UserAccess.of(Set.of(userRole), Set.of(usersModule), Set.of(readUsers));

		User existingUser = User.rehydrate(5L, "john", "old@x.com", true, "hashed", false, access);

		when(userCommandPort.findById(5L)).thenReturn(Optional.of(existingUser));
		when(userCommandPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UserChanges incoming = new UserChanges(null, "new@x.com", false, null, null, access);

		userCommandService.modifyUser(5L, incoming);

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userCommandPort).save(captor.capture());

		User saved = captor.getValue();
		assertThat(saved.id()).isEqualTo(5L);
		assertThat(saved.email()).isEqualTo("new@x.com");
		assertThat(saved.passwordHash()).isEqualTo("hashed");
		assertThat(saved.enabled()).isFalse();
		assertThat(saved.roles()).extracting(Roles::id, Roles::name).containsExactly(tuple(20L, "USER"));
		assertThat(saved.modules()).extracting(Modules::id, Modules::name).containsExactly(tuple(10L, "Users"));
		assertThat(saved.submodules()).extracting(Submodules::id, Submodules::name)
				.containsExactly(tuple(30L, "ReadUsers"));
	}

	@Test
	void modifyUser_whenPasswordProvided_encodesBeforeSave() {
		Modules usersModule = module(10L, "Users");
		Roles userRole = role(20L, "USER");
		Submodules readUsers = submodule(30L, "ReadUsers", usersModule);
		UserAccess access = UserAccess.of(Set.of(userRole), Set.of(usersModule), Set.of(readUsers));

		User existingUser = User.rehydrate(5L, "john", "old@x.com", true, "oldhash", false, access);
		when(userCommandPort.findById(5L)).thenReturn(Optional.of(existingUser));
		when(passwordEncoder.encode("newSecret123")).thenReturn("encodedSecret");
		when(userCommandPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		userCommandService.modifyUser(5L, new UserChanges(null, null, null, "newSecret123", null, null));

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userCommandPort).save(captor.capture());
		assertThat(captor.getValue().passwordHash()).isEqualTo("encodedSecret");
	}

	@Test
    void modifyUser_whenMissing_throwsEntityNotFound() {
        when(userCommandPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userCommandService.modifyUser(99L, new UserChanges(null, null, null, null, null, null)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

	@Test
	void createUser_assignsDefaultAccessAndEncodesPassword() {
		Modules usersModule = module(1L, "Users");
		Roles userRole = role(2L, "USER");
		Submodules readUsers = submodule(3L, "ReadUsers", usersModule);

		when(userUniquenessPolicy.usernameExists("john")).thenReturn(false);
		when(userUniquenessPolicy.emailExists("john@x.com")).thenReturn(false);
		when(defaultUserAccessPolicyService.defaultAccessForNewUser())
				.thenReturn(UserAccess.of(Set.of(userRole), Set.of(usersModule), Set.of(readUsers)));
		when(passwordEncoder.encode("secretPass1")).thenReturn("encodedPass");
		when(userCommandPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		User newUser = new User("john", "john@x.com", "secretPass1", false);

		userCommandService.createUser(newUser);

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userCommandPort).save(captor.capture());

		User saved = captor.getValue();
		assertThat(saved.enabled()).isTrue();
		assertThat(saved.passwordHash()).isEqualTo("encodedPass");
		assertThat(saved.roles()).extracting(Roles::id, Roles::name).containsExactly(tuple(2L, "USER"));
		assertThat(saved.modules()).extracting(Modules::id, Modules::name).containsExactly(tuple(1L, "Users"));
		assertThat(saved.submodules()).extracting(Submodules::id, Submodules::name)
				.containsExactly(tuple(3L, "ReadUsers"));
	}

	@Test
    void createUser_whenUsernameExists_throwsBusinessException() {
        when(userUniquenessPolicy.usernameExists("john")).thenReturn(true);

        assertThatThrownBy(() -> userCommandService.createUser(new User("john", "john@x.com", "secretPass1", false)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already in use");

        verifyNoInteractions(defaultUserAccessPolicyService, passwordEncoder);
    }

	private static Roles role(Long id, String name) {
		return Roles.reference(id, name);
	}

	private static org.assertj.core.groups.Tuple tuple(Object... values) {
		return org.assertj.core.groups.Tuple.tuple(values);
	}

	private static Modules module(Long id, String name) {
		return Modules.reference(id, name);
	}

	private static Submodules submodule(Long id, String name, Modules module) {
		return Submodules.reference(id, name, module);
	}
}
