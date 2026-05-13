package com.dossantosh.springfirstmodulith.users.api.controllers;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;
import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.security.AuthorizationService;
import com.dossantosh.springfirstmodulith.security.Permissions;
import com.dossantosh.springfirstmodulith.users.api.requests.CreateUserRequest;
import com.dossantosh.springfirstmodulith.users.api.requests.UpdateUserPersonalDataRequest;
import com.dossantosh.springfirstmodulith.users.api.requests.UpdateUserRolesRequest;
import com.dossantosh.springfirstmodulith.users.api.requests.UpdateUserRequest;
import com.dossantosh.springfirstmodulith.users.api.requests.UserAccessRequest;
import com.dossantosh.springfirstmodulith.users.application.services.UserAccessResolverService;
import com.dossantosh.springfirstmodulith.users.application.services.UserCommandService;
import com.dossantosh.springfirstmodulith.users.application.services.UserPersonalDataService;
import com.dossantosh.springfirstmodulith.users.application.services.UserQueryService;
import com.dossantosh.springfirstmodulith.users.application.views.UserDetailsView;
import com.dossantosh.springfirstmodulith.users.application.views.UserPersonalDataView;
import com.dossantosh.springfirstmodulith.users.application.views.UserRolesView;
import com.dossantosh.springfirstmodulith.users.application.views.UserSummaryView;
import com.dossantosh.springfirstmodulith.users.domain.EmployeeStatus;
import com.dossantosh.springfirstmodulith.users.domain.entities.Roles;
import com.dossantosh.springfirstmodulith.users.domain.entities.User;
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

	@jakarta.annotation.Resource
	private UserPersonalDataService userPersonalDataService;

	@BeforeEach
	void setUp() {
		reset(userCommandService, userAccessResolverService, userQueryService, userPersonalDataService);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_READ)
	void getUsers_whenUserHasSystemsReadScope_returnsUsers() {
		KeysetPage<UserSummaryView> page = new KeysetPage<>(
				List.of(new UserSummaryView(1L, "john", "john@example.com", true, false)), false, false, null, null);

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
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_READ)
	void getUserPersonalData_whenUserHasSystemsReadScope_returnsProfile() {
		UserPersonalDataView personalData = personalDataView(1L);
		when(userPersonalDataService.getPersonalData(1L)).thenReturn(personalData);

		var response = userController.getUserPersonalData(1L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(personalData);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_READ)
	void getUserRoles_whenUserHasSystemsReadScope_returnsRoles() {
		UserRolesView roles = rolesView(1L);
		when(userQueryService.getUserRoles(1L)).thenReturn(roles);

		var response = userController.getUserRoles(1L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(roles);
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
	void updateUserPersonalData_withoutSystemsWriteScope_isDenied() {
		UpdateUserPersonalDataRequest request = new UpdateUserPersonalDataRequest("EMP-7", "Ana", "Lopez",
				"ana@company.local", null, null, null, null, null, null, null, null, null, null, null,
				EmployeeStatus.ACTIVE, null, null);

		assertThatThrownBy(() -> userController.updateUserPersonalData(7L, request))
				.isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userPersonalDataService);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_WRITE)
	void updateUserPersonalData_withSystemsWriteScope_returnsProfile() {
		UpdateUserPersonalDataRequest request = new UpdateUserPersonalDataRequest("EMP-7", "Ana", "Lopez",
				"ana@company.local", null, null, null, null, null, null, null, null, null, null, null,
				EmployeeStatus.ACTIVE, null, null);
		UserPersonalDataView personalData = personalDataView(7L);

		when(userPersonalDataService.updatePersonalData(eq(7L), any())).thenReturn(personalData);

		var response = userController.updateUserPersonalData(7L, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(personalData);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_READ)
	void updateUserRoles_withoutSystemsWriteScope_isDenied() {
		assertThatThrownBy(() -> userController.updateUserRoles(7L, new UpdateUserRolesRequest(List.of(1L))))
				.isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userCommandService, userAccessResolverService, userQueryService);
	}

	@Test
	@WithMockUser(authorities = AuthorizationScopes.SYSTEMS_WRITE)
	void updateUserRoles_withSystemsWriteScope_returnsRoles() {
		User updated = User.rehydrate(7L, "john", "john@example.com", true, "secretPass1", false, null);
		UserRolesView roles = rolesView(7L);

		when(userAccessResolverService.resolve(List.of(1L))).thenReturn(userAccess());
		when(userCommandService.modifyUser(eq(7L), any())).thenReturn(updated);
		when(userQueryService.getUserRoles(7L)).thenReturn(roles);

		var response = userController.updateUserRoles(7L, new UpdateUserRolesRequest(List.of(1L)));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(roles);
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

	private UserPersonalDataView personalDataView(Long id) {
		return new UserPersonalDataView(id, "john", "EMP-" + id, "John", "Doe", "john@company.local", null, null, null,
				null, null, null, null, null, null, null, null, EmployeeStatus.ACTIVE, null, null);
	}

	private UserRolesView rolesView(Long id) {
		return new UserRolesView(id, "john", Set.of(), List.of());
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

		@Bean
		UserPersonalDataService userPersonalDataService() {
			return mock(UserPersonalDataService.class);
		}
	}
}
