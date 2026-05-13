package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.application.ports.out.RoleRepository;
import com.dossantosh.springfirstmodulith.users.domain.entities.Roles;
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
	private RoleRepository roleRepository;

	@InjectMocks
	private UserAccessResolverService userAccessResolverService;

	@Test
	void resolve_whenRoleIdsExist_returnsUserAccess() {
		Roles userRole = role(20L, "USER");

		when(roleRepository.findRolesById(List.of(20L))).thenReturn(List.of(userRole));

		UserAccess access = userAccessResolverService.resolve(List.of(20L));

		assertThat(access.roles()).containsExactly(userRole);
	}

	@Test
	void resolve_whenDuplicateRoleIdsProvided_deduplicatesBeforeLookup() {
		Roles userRole = role(20L, "USER");

		when(roleRepository.findRolesById(List.of(20L))).thenReturn(List.of(userRole));

		UserAccess access = userAccessResolverService.resolve(List.of(20L, 20L));

		assertThat(access.roles()).containsExactly(userRole);
	}

	@Test
	void resolve_whenAnyRoleIdMissing_throwsBusinessException() {
		when(roleRepository.findRolesById(List.of(20L))).thenReturn(List.of());

		assertThatThrownBy(() -> userAccessResolverService.resolve(List.of(20L)))
				.isInstanceOf(BusinessException.class).hasMessageContaining("roles");
	}

	@Test
	void resolve_whenRoleIdsAreEmpty_throwsBusinessException() {
		assertThatThrownBy(() -> userAccessResolverService.resolve(List.of())).isInstanceOf(BusinessException.class)
				.hasMessageContaining("roleIds");
	}

	private static Roles role(Long id, String name) {
		return Roles.reference(id, name);
	}
}
