package com.dossantosh.springfirstmodulith.integration.security;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;
import com.dossantosh.springfirstmodulith.security.AuthorizationService;
import com.dossantosh.springfirstmodulith.security.api.FeatureCapabilityResponse;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetails;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetailsService;
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

	@Test
	void dossantosh_keepsFullAccessFromAdminScopes() {
		CustomUserDetails user = loadUser("dossantosh");
		var authentication = authenticationFor(user);

		assertThat(user.getRoles()).containsExactly("ADMIN");
		assertThat(user.getScopes()).containsExactlyInAnyOrderElementsOf(AuthorizationScopes.ALL);
		assertThat(authorizationService.effectiveScopes(authentication))
				.containsExactlyInAnyOrderElementsOf(AuthorizationScopes.ALL);
		assertThat(authorizationService.capabilities(authentication).users())
				.isEqualTo(new FeatureCapabilityResponse(true, true, true, true, true));
		assertThat(authorizationService.capabilities(authentication).perfumes())
				.isEqualTo(new FeatureCapabilityResponse(true, true, true, true, true));
	}

	@Test
	void sevas_keepsOnlyShellAccessWithoutFunctionalScopes() {
		CustomUserDetails user = loadUser("sevas");
		var authentication = authenticationFor(user);

		assertThat(user.getRoles()).containsExactly("USER");
		assertThat(user.getScopes()).isEmpty();
		assertThat(user.getAuthorities()).isEmpty();
		assertThat(authorizationService.effectiveScopes(authentication)).isEmpty();
		assertThat(authorizationService.capabilities(authentication).users())
				.isEqualTo(new FeatureCapabilityResponse(false, false, false, false, false));
		assertThat(authorizationService.capabilities(authentication).perfumes())
				.isEqualTo(new FeatureCapabilityResponse(false, false, false, false, false));
	}

	private CustomUserDetails loadUser(String username) {
		return (CustomUserDetails) userDetailsService.loadUserByUsername(username);
	}

	private UsernamePasswordAuthenticationToken authenticationFor(CustomUserDetails user) {
		return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
	}
}
