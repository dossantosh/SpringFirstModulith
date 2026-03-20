package com.dossantosh.springfirstmodulith.users;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.application.services.DefaultUserAccessPolicyService;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
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
	void defaultAccessForNewUser_resolvesBusinessNamedDefaults() {
		Roles role = Roles.reference(1L, "USER");
		Modules module = Modules.reference(2L, "Users");
		Submodules submodule = Submodules.reference(3L, "ReadUsers", module);

		when(accessCatalog.findRoleByName("USER")).thenReturn(Optional.of(role));
		when(accessCatalog.findModuleByName("Users")).thenReturn(Optional.of(module));
		when(accessCatalog.findSubmoduleByModuleAndName("Users", "ReadUsers")).thenReturn(Optional.of(submodule));

		UserAccess access = policyService.defaultAccessForNewUser();

		assertThat(access.roles()).containsExactly(role);
		assertThat(access.modules()).containsExactly(module);
		assertThat(access.submodules()).containsExactly(submodule);
	}

	@Test
    void defaultAccessForNewUser_whenRoleMissing_throwsBusinessException() {
        when(accessCatalog.findRoleByName("USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.defaultAccessForNewUser())
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Default role 'USER' was not found");
    }
}
