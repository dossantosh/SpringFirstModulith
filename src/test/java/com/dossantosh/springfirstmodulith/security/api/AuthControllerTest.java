package com.dossantosh.springfirstmodulith.security.api;

import com.dossantosh.springfirstmodulith.security.session.CurrentSessionDataViewProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest {

	private final CurrentSessionDataViewProvider currentSessionDataViewProvider = new CurrentSessionDataViewProvider();
	private final AuthController controller = new AuthController(currentSessionDataViewProvider);

	@Test
	void me_returnsUsernameDataSourceAndCapabilitiesFromSession() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority("MODULE_USERS"), new SimpleGrantedAuthority("SUBMODULE_READUSERS")));
		MockHttpSession session = new MockHttpSession();
		currentSessionDataViewProvider.storeCurrentDataView(session, "historic");

		var response = controller.me(authentication, session);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.username()).isEqualTo("john");
		assertThat(body.dataSource()).isEqualTo("historic");
		assertThat(body.capabilities().users()).isEqualTo(new FeatureCapabilityResponse(true, true, false));
		assertThat(body.capabilities().perfumes()).isEqualTo(new FeatureCapabilityResponse(false, false, false));
	}

	@Test
	void me_returnsPerfumeCapabilitiesDerivedFromAuthorities() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority("MODULE_PERFUMES"),
						new SimpleGrantedAuthority("SUBMODULE_READPERFUMES"),
						new SimpleGrantedAuthority("SUBMODULE_WRITEPERFUMES")));

		var response = controller.me(authentication, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.capabilities().users()).isEqualTo(new FeatureCapabilityResponse(false, false, false));
		assertThat(body.capabilities().perfumes()).isEqualTo(new FeatureCapabilityResponse(true, true, true));
	}

	@Test
	void me_defaultsToProdWhenSessionDoesNotContainDataSource() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority("MODULE_USERS")));
		MockHttpSession session = new MockHttpSession();

		var response = controller.me(authentication, session);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.dataSource()).isEqualTo("prod");
	}

	@Test
	void me_returnsUnauthorizedWhenAuthenticationIsMissing() {
		var response = controller.me(null, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(response.getBody()).isNull();
	}
}
