package com.dossantosh.springfirstmodulith.security.api;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;
import com.dossantosh.springfirstmodulith.security.AuthorizationService;
import com.dossantosh.springfirstmodulith.security.SecurityAuthorityNames;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dossantosh.springfirstmodulith.security.session.CurrentSessionDataViewProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final CurrentSessionDataViewProvider currentSessionDataViewProvider = new CurrentSessionDataViewProvider();
	private final AuthorizationService authorizationService = new AuthorizationService();
	private final AuthController controller = new AuthController(currentSessionDataViewProvider, authorizationService);

	@Test
	void me_returnsUsernameDataSourceAndCapabilitiesFromSession() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority(SecurityAuthorityNames.ROLE_USER),
						new SimpleGrantedAuthority(AuthorizationScopes.USER_READ)));
		MockHttpSession session = new MockHttpSession();
		currentSessionDataViewProvider.storeCurrentDataView(session, "historic");

		var response = controller.me(authentication, session);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.username()).isEqualTo("john");
		assertThat(body.dataSource()).isEqualTo("historic");
		assertThat(body.roles()).containsExactly("USER");
		assertThat(body.scopes()).containsExactly(AuthorizationScopes.USER_READ);
		assertThat(body.capabilities().users())
				.isEqualTo(new FeatureCapabilityResponse(true, false, false, false, false));
		assertThat(body.capabilities().perfumes())
				.isEqualTo(new FeatureCapabilityResponse(false, false, false, false, false));
	}

	@Test
	void me_returnsPerfumeCapabilitiesDerivedFromAuthorities() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority(AuthorizationScopes.PERFUME_READ),
						new SimpleGrantedAuthority(AuthorizationScopes.PERFUME_CREATE),
						new SimpleGrantedAuthority(AuthorizationScopes.PERFUME_UPDATE),
						new SimpleGrantedAuthority(AuthorizationScopes.PERFUME_DELETE)));

		var response = controller.me(authentication, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.capabilities().users())
				.isEqualTo(new FeatureCapabilityResponse(false, false, false, false, false));
		assertThat(body.capabilities().perfumes())
				.isEqualTo(new FeatureCapabilityResponse(true, true, true, true, true));
	}

	@Test
	void me_serializesCapabilitiesWithoutExposingAuthorities() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority(AuthorizationScopes.USER_READ)));

		var response = controller.me(authentication, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var json = objectMapper.valueToTree(response.getBody());
		assertThat(json.has("authorities")).isFalse();
		assertThat(json.path("username").asText()).isEqualTo("john");
		assertThat(json.path("scopes").get(0).asText()).isEqualTo(AuthorizationScopes.USER_READ);
		assertThat(json.path("capabilities").path("users").path("access").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("users").path("read").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("users").path("write").asBoolean()).isFalse();
		assertThat(json.path("capabilities").path("users").path("canRead").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("users").path("canCreate").asBoolean()).isFalse();
	}

	@Test
	void me_defaultsToProdWhenSessionDoesNotContainDataSource() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority(AuthorizationScopes.USER_READ)));
		MockHttpSession session = new MockHttpSession();

		var response = controller.me(authentication, session);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.dataSource()).isEqualTo("prod");
	}

	@Test
	void me_doesNotGrantCapabilitiesFromLegacyAuthoritiesWithoutScopes() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority(SecurityAuthorityNames.SUBMODULE_READ_USERS)));

		var response = controller.me(authentication, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.capabilities().users())
				.isEqualTo(new FeatureCapabilityResponse(false, false, false, false, false));
	}

	@Test
	void me_returnsUnauthorizedWhenAuthenticationIsMissing() {
		var response = controller.me(null, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(response.getBody()).isNull();
	}
}
