package com.dossantosh.springfirstmodulith.users;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.security.Permissions;
import com.dossantosh.springfirstmodulith.security.SecurityAuthorityNames;
import com.dossantosh.springfirstmodulith.users.api.controllers.UserController;
import com.dossantosh.springfirstmodulith.users.api.requests.CreateUserRequest;
import com.dossantosh.springfirstmodulith.users.api.requests.UpdateUserRequest;
import com.dossantosh.springfirstmodulith.users.application.services.UserAccessResolverService;
import com.dossantosh.springfirstmodulith.users.application.services.UserCommandService;
import com.dossantosh.springfirstmodulith.users.application.services.UserQueryService;
import com.dossantosh.springfirstmodulith.users.application.views.UserDetailsView;
import com.dossantosh.springfirstmodulith.users.application.views.UserSummaryView;
import com.dossantosh.springfirstmodulith.users.domain.User;
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

@SpringJUnitConfig(classes = {UserController.class, Permissions.class, UserControllerAuthorizationTest.TestConfig.class})
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
	@WithMockUser(authorities = {SecurityAuthorityNames.MODULE_USERS, SecurityAuthorityNames.SUBMODULE_READ_USERS})
	void getUsers_whenUserCanReadUsers_returnsUsers() {
		KeysetPage<UserSummaryView> page = new KeysetPage<>();
		page.setContent(List.of(new UserSummaryView(1L, "john", "john@example.com", true, false)));

		when(userQueryService.findUsersKeyset(null, null, null, null, 25, Direction.NEXT)).thenReturn(page);

		var response = userController.getUsers(null, null, null, null, 25, "NEXT");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(page);
	}

	@Test
	@WithMockUser(authorities = {SecurityAuthorityNames.MODULE_USERS, SecurityAuthorityNames.SUBMODULE_READ_USERS})
	void getUserDetails_whenUserCanReadUsers_returnsDetails() {
		UserDetailsView details = detailsView(1L);
		when(userQueryService.getUserDetails(1L)).thenReturn(details);

		var response = userController.getUserDetails(1L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(details);
	}

	@Test
	@WithMockUser(authorities = {SecurityAuthorityNames.MODULE_USERS})
	void getUsers_whenUserCannotReadUsers_isDenied() {
		assertThatThrownBy(() -> userController.getUsers(null, null, null, null, 25, "NEXT"))
				.isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userQueryService);
	}

	@Test
	@WithMockUser(authorities = {SecurityAuthorityNames.MODULE_USERS, SecurityAuthorityNames.SUBMODULE_WRITE_USERS})
	void createUser_whenUserCanWriteUsers_returnsCreatedDetails() {
		User created = User.rehydrate(7L, "john", "john@example.com", true, "secretPass1", false, null);
		UserDetailsView details = detailsView(7L);

		when(userCommandService.createUser(any(User.class))).thenReturn(created);
		when(userQueryService.getUserDetails(7L)).thenReturn(details);

		var response = userController.createUser(new CreateUserRequest("john", "john@example.com", "secretPass1", false,
				null));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isSameAs(details);
	}

	@Test
	@WithMockUser(authorities = {SecurityAuthorityNames.MODULE_USERS, SecurityAuthorityNames.SUBMODULE_READ_USERS})
	void createUser_whenUserCannotWriteUsers_isDenied() {
		assertThatThrownBy(() -> userController.createUser(new CreateUserRequest("john", "john@example.com",
				"secretPass1", false, null))).isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userCommandService, userAccessResolverService, userQueryService);
	}

	@Test
	@WithMockUser(authorities = {SecurityAuthorityNames.MODULE_USERS, SecurityAuthorityNames.SUBMODULE_READ_USERS})
	void updateUser_whenUserCannotWriteUsers_isDenied() {
		assertThatThrownBy(() -> userController.updateUser(7L,
				new UpdateUserRequest("john", "john@example.com", true, null, false, null)))
			.isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userCommandService, userAccessResolverService, userQueryService);
	}

	@Test
	@WithMockUser(authorities = {SecurityAuthorityNames.MODULE_USERS, SecurityAuthorityNames.SUBMODULE_READ_USERS})
	void deleteUser_whenUserCannotWriteUsers_isDenied() {
		assertThatThrownBy(() -> userController.deleteUser(7L)).isInstanceOf(AccessDeniedException.class);

		verifyNoInteractions(userCommandService);
	}

	private UserDetailsView detailsView(Long id) {
		return new UserDetailsView(id, "john", "john@example.com", true, false, Set.of(), Set.of(), Set.of());
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
