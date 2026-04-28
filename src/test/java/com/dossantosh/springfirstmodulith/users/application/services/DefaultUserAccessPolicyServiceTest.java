package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import com.dossantosh.springfirstmodulith.users.domain.ports.AccessCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUserAccessPolicyServiceTest {

	@Mock
	private AccessCatalog accessCatalog;

	@InjectMocks
	private DefaultUserAccessPolicyService policyService;

	@Test
	void defaultAccessForNewUser_resolvesDefaultRoleOnly() {
		Roles role = Roles.reference(1L, "USER");

		when(accessCatalog.findRoleByName("USER")).thenReturn(Optional.of(role));

		UserAccess access = policyService.defaultAccessForNewUser();

		assertThat(access.roles()).containsExactly(role);
		assertThat(access.modules()).isEmpty();
		assertThat(access.submodules()).isEmpty();
	}

	@Test
	void defaultAccessForNewUser_whenRoleMissing_throwsBusinessException() {
		when(accessCatalog.findRoleByName("USER")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> policyService.defaultAccessForNewUser()).isInstanceOf(BusinessException.class)
				.hasMessageContaining("Default role 'USER' was not found");
	}
}
