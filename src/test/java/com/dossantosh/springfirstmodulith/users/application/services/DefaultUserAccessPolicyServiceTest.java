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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUserAccessPolicyServiceTest {

	@Mock
	private RoleRepository roleRepository;

	@InjectMocks
	private DefaultUserAccessPolicyService policyService;

	@Test
	void defaultAccessForNewUser_resolvesDefaultRoleOnly() {
		Roles role = Roles.reference(1L, "SYSTEMS");

		when(roleRepository.findRoleByName("SYSTEMS")).thenReturn(Optional.of(role));

		UserAccess access = policyService.defaultAccessForNewUser();

		assertThat(access.roles()).containsExactly(role);
	}

	@Test
	void defaultAccessForNewUser_whenRoleMissing_throwsBusinessException() {
		when(roleRepository.findRoleByName("SYSTEMS")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> policyService.defaultAccessForNewUser()).isInstanceOf(BusinessException.class)
				.hasMessageContaining("Default role 'SYSTEMS' was not found");
	}
}
