package com.dossantosh.springfirstmodulith.users.api.controllers;

import com.dossantosh.springfirstmodulith.core.exceptions.GlobalExceptionHandler;
import com.dossantosh.springfirstmodulith.users.application.services.UserAccessResolverService;
import com.dossantosh.springfirstmodulith.users.application.services.UserCommandService;
import com.dossantosh.springfirstmodulith.users.application.services.UserQueryService;
import com.dossantosh.springfirstmodulith.users.application.views.ModuleView;
import com.dossantosh.springfirstmodulith.users.application.views.RoleView;
import com.dossantosh.springfirstmodulith.users.application.views.SubmoduleView;
import com.dossantosh.springfirstmodulith.users.application.views.UserDetailsView;
import com.dossantosh.springfirstmodulith.users.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	private MockMvc mockMvc;

	@Mock
	private UserCommandService userCommandService;

	@Mock
	private UserAccessResolverService userAccessResolverService;

	@Mock
	private UserQueryService userQueryService;

	@InjectMocks
	private UserController userController;

	@BeforeEach
	void setUp() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		this.mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(new GlobalExceptionHandler())
				.setValidator(validator).build();
	}

	@Test
	void createUser_whenValidRequest_returnsCreatedDetails() throws Exception {
		Modules users = module(10L, "Users");
		Roles userRole = role(20L, "USER");
		Submodules readUsers = submodule(30L, "ReadUsers", users);
		UserAccess access = UserAccess.of(Set.of(userRole), Set.of(users), Set.of(readUsers));

		when(userAccessResolverService.resolve(List.of(20L), List.of(10L), List.of(30L))).thenReturn(access);
		when(userCommandService.createUser(any(User.class)))
				.thenReturn(User.rehydrate(99L, "john", "john@x.com", true, "hashed", false, access));
		when(userQueryService.getUserDetails(99L)).thenReturn(detailsView(99L, "john", "john@x.com"));

		String body = """
				{
				  "username":"john",
				  "email":"john@x.com",
				  "password":"secretPass1",
				  "isAdmin":false,
				  "access":{
				    "roleIds":[20],
				    "moduleIds":[10],
				    "submoduleIds":[30]
				  }
				}
				""";

		mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(99))
				.andExpect(jsonPath("$.username").value("john"));
	}

	@Test
    void updateUser_whenValidRequest_returnsUpdatedDetails() throws Exception {
        when(userCommandService.modifyUser(any(Long.class), any()))
                .thenReturn(User.rehydrate(5L, "johnny", "johnny@x.com", true, "hashed", false, null));
        when(userQueryService.getUserDetails(5L)).thenReturn(detailsView(5L, "johnny", "johnny@x.com"));

        String body = """
                {
                  "username":"johnny",
                  "email":"johnny@x.com",
                  "enabled":true
                }
                """;

        mockMvc.perform(put("/api/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.username").value("johnny"));

        verifyNoInteractions(userAccessResolverService);
    }

	@Test
	void deleteUser_returnsNoContent() throws Exception {
		mockMvc.perform(delete("/api/users/7")).andExpect(status().isNoContent());

		verify(userCommandService).deleteById(7L);
	}

	@Test
	void createUser_whenInvalidRequest_returnsBadRequest() throws Exception {
		String invalidBody = """
				{
				  "username":"",
				  "email":"not-an-email",
				  "password":"123",
				  "isAdmin":null
				}
				""";

		mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(invalidBody))
				.andExpect(status().isBadRequest());
	}

	private UserDetailsView detailsView(Long id, String username, String email) {
		return new UserDetailsView(id, username, email, true, false, Set.of(new RoleView(20L, "USER")),
				Set.of(new ModuleView(10L, "Users")), Set.of(new SubmoduleView(30L, "ReadUsers")));
	}

	private static Roles role(Long id, String name) {
		return Roles.reference(id, name);
	}

	private static Modules module(Long id, String name) {
		return Modules.reference(id, name);
	}

	private static Submodules submodule(Long id, String name, Modules module) {
		return Submodules.reference(id, name, module);
	}
}
