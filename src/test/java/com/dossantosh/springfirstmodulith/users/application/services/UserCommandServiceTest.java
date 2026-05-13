package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserRepository;
import com.dossantosh.springfirstmodulith.users.domain.*;
import com.dossantosh.springfirstmodulith.users.domain.entities.Roles;
import com.dossantosh.springfirstmodulith.users.domain.entities.User;
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
	private UserRepository userRepository;

	@Mock
	private DefaultUserAccessPolicyService defaultUserAccessPolicyService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserCommandService userCommandService;

	@Test
	void modifyUser_whenValidChangesProvided_updatesExistingAggregateAndSaves() {
		Roles userRole = role(20L, "USER");
		UserAccess access = UserAccess.of(Set.of(userRole));

		User existingUser = User.rehydrate(5L, "john", "old@x.com", true, "hashed", false, access);

		when(userRepository.findById(5L)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UserChanges incoming = new UserChanges(null, "new@x.com", false, null, null, access);

		userCommandService.modifyUser(5L, incoming);

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(captor.capture());

		User saved = captor.getValue();
		assertThat(saved.id()).isEqualTo(5L);
		assertThat(saved.email()).isEqualTo("new@x.com");
		assertThat(saved.passwordHash()).isEqualTo("hashed");
		assertThat(saved.enabled()).isFalse();
		assertThat(saved.roles()).extracting(Roles::id, Roles::name).containsExactly(tuple(20L, "USER"));
	}

	@Test
	void modifyUser_whenPasswordProvided_encodesBeforeSave() {
		Roles userRole = role(20L, "USER");
		UserAccess access = UserAccess.of(Set.of(userRole));

		User existingUser = User.rehydrate(5L, "john", "old@x.com", true, "oldhash", false, access);
		when(userRepository.findById(5L)).thenReturn(Optional.of(existingUser));
		when(passwordEncoder.encode("newSecret123")).thenReturn("encodedSecret");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		userCommandService.modifyUser(5L, new UserChanges(null, null, null, "newSecret123", null, null));

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(captor.capture());
		assertThat(captor.getValue().passwordHash()).isEqualTo("encodedSecret");
	}

	@Test
    void modifyUser_whenMissing_throwsEntityNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userCommandService.modifyUser(99L, new UserChanges(null, null, null, null, null, null)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

	@Test
	void createUser_assignsDefaultAccessAndEncodesPassword() {
		Roles userRole = role(2L, "USER");

		when(userRepository.usernameExists("john")).thenReturn(false);
		when(userRepository.emailExists("john@x.com")).thenReturn(false);
		when(defaultUserAccessPolicyService.defaultAccessForNewUser()).thenReturn(UserAccess.of(Set.of(userRole)));
		when(passwordEncoder.encode("secretPass1")).thenReturn("encodedPass");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		User newUser = new User("john", "john@x.com", "secretPass1", false);

		userCommandService.createUser(newUser);

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(captor.capture());

		User saved = captor.getValue();
		assertThat(saved.enabled()).isTrue();
		assertThat(saved.passwordHash()).isEqualTo("encodedPass");
		assertThat(saved.roles()).extracting(Roles::id, Roles::name).containsExactly(tuple(2L, "USER"));
	}

	@Test
    void createUser_whenUsernameExists_throwsBusinessException() {
        when(userRepository.usernameExists("john")).thenReturn(true);

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
}
