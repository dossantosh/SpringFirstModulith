package com.dossantosh.springfirstmodulith.users.api.controllers;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;
import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.security.AuthorizationService;
import com.dossantosh.springfirstmodulith.security.Permissions;
import com.dossantosh.springfirstmodulith.users.api.requests.CreateUserRequest;
import com.dossantosh.springfirstmodulith.users.api.requests.UpdateUserRequest;
import com.dossantosh.springfirstmodulith.users.api.requests.UserAccessRequest;
import com.dossantosh.springfirstmodulith.users.application.services.UserAccessResolverService;
import com.dossantosh.springfirstmodulith.users.application.services.UserCommandService;
import com.dossantosh.springfirstmodulith.users.application.services.UserQueryService;
import com.dossantosh.springfirstmodulith.users.application.views.UserDetailsView;
import com.dossantosh.springfirstmodulith.users.application.views.UserSummaryView;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = {UserController.class, Permissions.class, AuthorizationService.class,
		UserControllerAuthorizationTest.TestConfig.class})
class UserControllerAuthorizationTest {

	@jakarta.annotation.Resource
	private UserController userController;

	@jakarta.annotation.Resource
	private UserCommandService userCommandService;

	@jakarta.annotation.Resource
	private UserAccessResolverService userAccessResolverService;

	@jakarta.annotation.Resource
	private UserQueryService userQueryService;

	@BeforeEach
	void setUp() {
		reset(userCommandService, userAccessResolverService, userQueryService);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_READ)
	void getUsers_whenUserHasSystemsReadScope_returnsUsers() {
		KeysetPage<UserSummaryView> page = new KeysetPage<>();
		page.setContent(List.of(new UserSummaryView(1L, "john", "john@example.com", true, false)));

		when(userQueryService.findUsersKeyset(null, null, null, null, 25, Direction.NEXT)).thenReturn(page);

		var response = userController.getUsers(null, null, null, null, 25, "NEXT");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(page);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_READ)
	void getUserDetails_whenUserHasSystemsReadScope_returnsDetails() {
		UserDetailsView details = detailsView(1L);
		when(userQueryService.getUserDetails(1L)).thenReturn(details);

		var response = userController.getUserDetails(1L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(details);
	}

	@Test
	@WithMockUser
	void getUsers_withoutSystemsReadScope_isDenied() {
		assertThatThrownBy(() -> userController.getUsers(null, null, null, null, 25, "NEXT"))
				.isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userQueryService);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_WRITE)
	void createUser_withoutAccessChangeWhenUserHasSystemsWriteScope_returnsCreatedDetails() {
		User created = User.rehydrate(7L, "john", "john@example.com", true, "secretPass1", false, null);
		UserDetailsView details = detailsView(7L);

		when(userCommandService.createUser(any(User.class))).thenReturn(created);
		when(userQueryService.getUserDetails(7L)).thenReturn(details);

		var response = userController
				.createUser(new CreateUserRequest("john", "john@example.com", "secretPass1", false, null));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isSameAs(details);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_READ)
	void createUser_withAccessChangeWithoutSystemsWriteScope_isDenied() {
		assertThatThrownBy(() -> userController.createUser(createRequestWithAccess()))
				.isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userCommandService, userAccessResolverService, userQueryService);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_WRITE)
	void createUser_withAccessChangeWhenUserHasSystemsWriteScope_returnsCreatedDetails() {
		User created = User.rehydrate(7L, "john", "john@example.com", true, "secretPass1", false, null);
		UserDetailsView details = detailsView(7L);

		when(userAccessResolverService.resolve(List.of(1L))).thenReturn(userAccess());
		when(userCommandService.createUser(any(User.class))).thenReturn(created);
		when(userQueryService.getUserDetails(7L)).thenReturn(details);

		var response = userController.createUser(createRequestWithAccess());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isSameAs(details);
		verify(userAccessResolverService).resolve(List.of(1L));
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_READ)
	void createUser_withoutSystemsWriteScope_isDenied() {
		assertThatThrownBy(() -> userController
				.createUser(new CreateUserRequest("john", "john@example.com", "secretPass1", false, null)))
				.isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userCommandService, userAccessResolverService, userQueryService);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_READ)
	void updateUser_withoutSystemsWriteScope_isDenied() {
		assertThatThrownBy(() -> userController.updateUser(7L,
				new UpdateUserRequest("john", "john@example.com", true, null, false, null)))
				.isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userCommandService, userAccessResolverService, userQueryService);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_READ)
	void updateUser_withAccessChangeWithoutSystemsWriteScope_isDenied() {
		assertThatThrownBy(() -> userController.updateUser(7L,
				new UpdateUserRequest("john", "john@example.com", true, null, false, accessRequest())))
				.isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userCommandService, userAccessResolverService, userQueryService);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_WRITE)
	void updateUser_withAccessChangeWhenUserHasSystemsWriteScope_returnsDetails() {
		User updated = User.rehydrate(7L, "john", "john@example.com", true, "secretPass1", false, null);
		UserDetailsView details = detailsView(7L);

		when(userAccessResolverService.resolve(List.of(1L))).thenReturn(userAccess());
		when(userCommandService.modifyUser(eq(7L), any())).thenReturn(updated);
		when(userQueryService.getUserDetails(7L)).thenReturn(details);

		var response = userController.updateUser(7L,
				new UpdateUserRequest("john", "john@example.com", true, null, false, accessRequest()));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(details);
		verify(userAccessResolverService).resolve(List.of(1L));
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_READ)
	void deleteUser_withoutSystemsWriteScope_isDenied() {
		assertThatThrownBy(() -> userController.deleteUser(7L)).isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userCommandService);
	}

	private UserDetailsView detailsView(Long id) {
		return new UserDetailsView(id, "john", "john@example.com", true, false, Set.of());
	}

	private CreateUserRequest createRequestWithAccess() {
		return new CreateUserRequest("john", "john@example.com", "secretPass1", false, accessRequest());
	}

	private UserAccessRequest accessRequest() {
		return new UserAccessRequest(List.of(1L));
	}

	private UserAccess userAccess() {
		return UserAccess.of(Set.of(Roles.reference(1L, "SYSTEMS")));
	}

	@Configuration
	@EnableMethodSecurity
	static class TestConfig {

		@Bean
		UserCommandService userCommandService() {
			return mock(UserCommandService.class);
		}

		@Bean
		UserAccessResolverService userAccessResolverService() {
			return mock(UserAccessResolverService.class);
		}

		@Bean
		UserQueryService userQueryService() {
			return mock(UserQueryService.class);
		}
	}
}

