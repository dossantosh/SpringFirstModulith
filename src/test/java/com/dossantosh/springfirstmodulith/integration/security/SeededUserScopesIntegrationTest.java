package com.dossantosh.springfirstmodulith.integration.security;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;
import com.dossantosh.springfirstmodulith.security.AuthorizationService;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetails;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetailsService;
import com.dossantosh.springfirstmodulith.users.api.ports.navigation.NavigationCatalogQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SeededUserScopesIntegrationTest {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private NavigationCatalogQuery navigationCatalogQuery;

	@Test
	void dossantosh_keepsFullAccessFromModuleRoles() {
		CustomUserDetails user = loadUser("dossantosh");
		var authentication = authenticationFor(user);

		assertThat(user.getRoles()).containsExactlyInAnyOrder("SYSTEMS", "PERFUMES");
		assertThat(user.getScopes()).containsExactlyInAnyOrderElementsOf(AuthorizationScopes.ALL);
		assertThat(authorizationService.effectiveScopes(authentication))
				.containsExactlyInAnyOrderElementsOf(AuthorizationScopes.ALL);
		assertThat(navigationCatalogQuery.findVisibleNavigation(user.getScopes())).extracting("key")
				.containsExactly("systems", "perfumes");
	}

	@Test
	void sevas_keepsOnlySystemsAccess() {
		CustomUserDetails user = loadUser("sevas");
		var authentication = authenticationFor(user);

		assertThat(user.getRoles()).containsExactly("SYSTEMS");
		assertThat(user.getScopes()).containsExactlyInAnyOrder(AuthorizationScopes.SYSTEMS_READ,
				AuthorizationScopes.SYSTEMS_WRITE);
		assertThat(user.getAuthorities()).extracting("authority")
				.containsExactlyInAnyOrder(AuthorizationScopes.SYSTEMS_READ, AuthorizationScopes.SYSTEMS_WRITE);
		assertThat(authorizationService.effectiveScopes(authentication))
				.containsExactly(AuthorizationScopes.SYSTEMS_READ, AuthorizationScopes.SYSTEMS_WRITE);
		assertThat(navigationCatalogQuery.findVisibleNavigation(user.getScopes())).extracting("key")
				.containsExactly("systems");
	}

	private CustomUserDetails loadUser(String username) {
		return (CustomUserDetails) userDetailsService.loadUserByUsername(username);
	}

	private UsernamePasswordAuthenticationToken authenticationFor(CustomUserDetails user) {
		return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
	}
}

