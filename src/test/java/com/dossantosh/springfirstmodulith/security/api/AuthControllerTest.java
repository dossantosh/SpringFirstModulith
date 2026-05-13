package com.dossantosh.springfirstmodulith.security.api;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;
import com.dossantosh.springfirstmodulith.security.AuthorizationService;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetails;
import com.dossantosh.springfirstmodulith.users.api.ports.navigation.NavigationCatalogQuery;
import com.dossantosh.springfirstmodulith.users.api.ports.navigation.NavigationItemView;
import com.dossantosh.springfirstmodulith.users.api.ports.navigation.NavigationModuleView;
import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthView;
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
	private final NavigationCatalogQuery navigationCatalogQuery = this::navigationForScopes;
	private final AuthController controller = new AuthController(currentSessionDataViewProvider, authorizationService,
			navigationCatalogQuery);

	@Test
	void me_returnsUsernameDataSourceScopesAndNavigationFromSession() {
		CustomUserDetails userDetails = customUserDetails("john", List.of("SYSTEMS"),
				List.of(AuthorizationScopes.SYSTEMS_READ));
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, "n/a",
				userDetails.getAuthorities());
		MockHttpSession session = new MockHttpSession();
		currentSessionDataViewProvider.storeCurrentDataView(session, "historic");

		var response = controller.me(authentication, session);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.username()).isEqualTo("john");
		assertThat(body.dataSource()).isEqualTo("historic");
		assertThat(body.roles()).containsExactly("SYSTEMS");
		assertThat(body.scopes()).containsExactly(AuthorizationScopes.SYSTEMS_READ);
		assertThat(body.navigation()).hasSize(1);
		assertThat(body.navigation().getFirst().key()).isEqualTo("systems");
		assertThat(body.navigation().getFirst().items()).extracting(NavigationItemResponse::key)
				.containsExactly("users_search");
	}

	@Test
	void me_returnsPerfumeNavigationDerivedFromScopes() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority(AuthorizationScopes.PERFUMES_READ),
						new SimpleGrantedAuthority(AuthorizationScopes.PERFUMES_WRITE)));

		var response = controller.me(authentication, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.scopes()).containsExactly(AuthorizationScopes.PERFUMES_READ,
				AuthorizationScopes.PERFUMES_WRITE);
		assertThat(body.navigation()).hasSize(1);
		assertThat(body.navigation().getFirst().key()).isEqualTo("perfumes");
		assertThat(body.navigation().getFirst().items()).extracting(NavigationItemResponse::key)
				.containsExactly("perfumes_catalog");
	}

	@Test
	void me_serializesScopesWithoutExposingAuthorities() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority(AuthorizationScopes.SYSTEMS_READ)));

		var response = controller.me(authentication, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var json = objectMapper.valueToTree(response.getBody());
		assertThat(json.has("authorities")).isFalse();
		assertThat(json.path("username").asText()).isEqualTo("john");
		assertThat(json.path("scopes").get(0).asText()).isEqualTo(AuthorizationScopes.SYSTEMS_READ);
		assertThat(json.path("navigation").get(0).path("items").get(0).path("route").asText())
				.isEqualTo("/users/search");
	}

	@Test
	void me_defaultsToProdWhenSessionDoesNotContainDataSource() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority(AuthorizationScopes.SYSTEMS_READ)));
		MockHttpSession session = new MockHttpSession();

		var response = controller.me(authentication, session);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.dataSource()).isEqualTo("prod");
	}

	@Test
	void me_ignoresLegacyAuthoritiesWithoutScopes() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority("SUBMODULE_USERS_SEARCH")));

		var response = controller.me(authentication, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.scopes()).isEmpty();
		assertThat(body.navigation()).isEmpty();
	}

	@Test
	void me_returnsUnauthorizedWhenAuthenticationIsMissing() {
		var response = controller.me(null, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(response.getBody()).isNull();
	}

	private CustomUserDetails customUserDetails(String username, List<String> roles, List<String> scopes) {
		return CustomUserDetails
				.from(new UserAuthView(1L, username, username + "@example.com", "secret", true, false, roles, scopes));
	}

	private List<NavigationModuleView> navigationForScopes(java.util.Collection<String> scopes) {
		if (scopes.contains(AuthorizationScopes.SYSTEMS_READ)) {
			return List.of(new NavigationModuleView("systems", "Sistemas", "settings", List
					.of(new NavigationItemView("users_search", "Usuarios", "group", "/users/search", false, null))));
		}
		if (scopes.contains(AuthorizationScopes.PERFUMES_READ)) {
			return List.of(new NavigationModuleView("perfumes", "Perfumes", "local_florist",
					List.of(new NavigationItemView("perfumes_catalog", "Catálogo", "local_florist", "/perfumes/catalog",
							true, "Módulo previsto para próximas fases"))));
		}
		return List.of();
	}
}
