package com.dossantosh.springfirstmodulith.auth.controllers;

import com.dossantosh.springfirstmodulith.core.datasource.runtime.DataViewFromSessionFilter;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest {

	private final AuthController controller = new AuthController();

	@Test
	void me_returnsUsernameAuthoritiesAndDataSourceFromSession() {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "n/a",
				List.of(new SimpleGrantedAuthority("MODULE_USERS")));
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(DataViewFromSessionFilter.SESSION_KEY, "historic");

		var response = controller.me(authentication, session);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(AuthSessionResponse.class);

		AuthSessionResponse body = (AuthSessionResponse) response.getBody();
		assertThat(body.username()).isEqualTo("john");
		assertThat(body.authorities()).containsExactly("MODULE_USERS");
		assertThat(body.dataSource()).isEqualTo("historic");
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
