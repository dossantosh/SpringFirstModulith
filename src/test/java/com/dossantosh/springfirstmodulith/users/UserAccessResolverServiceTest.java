package com.dossantosh.springfirstmodulith.users;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserAccessLookupPort;
import com.dossantosh.springfirstmodulith.users.application.services.UserAccessResolverService;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccessResolverServiceTest {

	@Mock
	private UserAccessLookupPort userAccessLookupPort;

	@InjectMocks
	private UserAccessResolverService userAccessResolverService;

	@Test
	void resolve_whenAllIdsExist_returnsUserAccess() {
		Modules users = module(10L, "Users");
		Roles userRole = role(20L, "USER");
		Submodules readUsers = submodule(30L, "ReadUsers", users);

		when(userAccessLookupPort.findRolesById(List.of(20L))).thenReturn(List.of(userRole));
		when(userAccessLookupPort.findModulesById(List.of(10L))).thenReturn(List.of(users));
		when(userAccessLookupPort.findSubmodulesById(List.of(30L))).thenReturn(List.of(readUsers));

		UserAccess access = userAccessResolverService.resolve(List.of(20L), List.of(10L), List.of(30L));

		assertThat(access.roles()).containsExactly(userRole);
		assertThat(access.modules()).containsExactly(users);
		assertThat(access.submodules()).containsExactly(readUsers);
	}

	@Test
    void resolve_whenAnyIdMissing_throwsBusinessException() {
        when(userAccessLookupPort.findRolesById(List.of(20L))).thenReturn(List.of());
        when(userAccessLookupPort.findModulesById(List.of(10L))).thenReturn(List.of(module(10L, "Users")));
        when(userAccessLookupPort.findSubmodulesById(List.of(30L))).thenReturn(List.of(submodule(30L, "ReadUsers", module(10L, "Users"))));

        assertThatThrownBy(() -> userAccessResolverService.resolve(List.of(20L), List.of(10L), List.of(30L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("roles");
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
