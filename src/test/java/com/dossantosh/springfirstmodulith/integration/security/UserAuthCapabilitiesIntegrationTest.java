package com.dossantosh.springfirstmodulith.integration.security;

import com.dossantosh.springfirstmodulith.SpringfirstmodulithApplication;
import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;
import com.dossantosh.springfirstmodulith.security.AuthorizationService;
import com.dossantosh.springfirstmodulith.security.api.AuthController;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetailsService;
import com.dossantosh.springfirstmodulith.security.session.CurrentSessionDataViewProvider;
import com.dossantosh.springfirstmodulith.users.api.ports.navigation.NavigationCatalogQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = SpringfirstmodulithApplication.class)
@Import({UserAuthCapabilitiesIntegrationTest.TestConfig.class, CustomUserDetailsService.class,
		CurrentSessionDataViewProvider.class, AuthorizationService.class, AuthController.class})
@TestPropertySource(properties = {"spring.datasource.url=jdbc:tc:postgresql:17-alpine:///testdb",
		"spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
		"spring.jpa.hibernate.ddl-auto=create-drop"})
class UserAuthCapabilitiesIntegrationTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private AuthController authController;

	@PersistenceContext
	private EntityManager em;

	@TestConfiguration
	@ComponentScan(basePackages = "com.dossantosh.springfirstmodulith.users.infrastructure.adapters")
	static class TestConfig {

		@Bean
		@Primary
		NavigationCatalogQuery navigationCatalogQuery() {
			return scopes -> java.util.List.of();
		}
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void me_mapsRoleScopesToCapabilitiesWithoutModuleOrSubmoduleAssignments() {
		long userId = insertUser("john", "john@example.com", "hashedpw", true, false);
		long systemsRole = insertRole("SYSTEMS");
		long perfumesRole = insertRole("PERFUMES");
		long systemsRead = insertScope(AuthorizationScopes.SYSTEMS_READ);
		long systemsWrite = insertScope(AuthorizationScopes.SYSTEMS_WRITE);
		long perfumeRead = insertScope(AuthorizationScopes.PERFUMES_READ);
		long perfumeWrite = insertScope(AuthorizationScopes.PERFUMES_WRITE);

		linkUserRole(userId, systemsRole);
		linkUserRole(userId, perfumesRole);
		linkRoleScope(systemsRole, systemsRead);
		linkRoleScope(systemsRole, systemsWrite);
		linkRoleScope(perfumesRole, perfumeRead);
		linkRoleScope(perfumesRole, perfumeWrite);

		em.flush();
		em.clear();

		UserDetails userDetails = userDetailsService.loadUserByUsername("john");

		assertThat(userDetails.getAuthorities()).extracting("authority").containsExactlyInAnyOrder(
				AuthorizationScopes.SYSTEMS_READ, AuthorizationScopes.SYSTEMS_WRITE, AuthorizationScopes.PERFUMES_READ,
				AuthorizationScopes.PERFUMES_WRITE);

		var authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		var response = authController.me(authentication, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var json = objectMapper.valueToTree(response.getBody());
		assertThat(json.has("authorities")).isFalse();
		assertThat(json.path("scopes").size()).isEqualTo(4);
		assertThat(json.path("username").asText()).isEqualTo("john");
		assertThat(java.util.stream.StreamSupport.stream(json.path("roles").spliterator(), false)
				.map(com.fasterxml.jackson.databind.JsonNode::asText).toList()).containsExactlyInAnyOrder("SYSTEMS",
						"PERFUMES");
		assertThat(json.path("capabilities").path("systems").has("access")).isFalse();
		assertThat(json.path("capabilities").path("systems").has("read")).isFalse();
		assertThat(json.path("capabilities").path("systems").has("write")).isFalse();
		assertThat(json.path("capabilities").path("systems").path("canRead").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("systems").path("canWrite").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("systems").has("canAccess")).isFalse();
		assertThat(json.path("capabilities").path("systems").has("canCreate")).isFalse();
		assertThat(json.path("capabilities").path("systems").has("canUpdate")).isFalse();
		assertThat(json.path("capabilities").path("systems").has("canDelete")).isFalse();
		assertThat(json.path("capabilities").path("perfumes").has("access")).isFalse();
		assertThat(json.path("capabilities").path("perfumes").has("read")).isFalse();
		assertThat(json.path("capabilities").path("perfumes").has("write")).isFalse();
		assertThat(json.path("capabilities").path("perfumes").path("canRead").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("perfumes").path("canWrite").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("perfumes").has("canAccess")).isFalse();
		assertThat(json.path("capabilities").path("perfumes").has("canCreate")).isFalse();
		assertThat(json.path("capabilities").path("perfumes").has("canUpdate")).isFalse();
		assertThat(json.path("capabilities").path("perfumes").has("canDelete")).isFalse();
	}

	@Test
	void me_doesNotGrantCapabilitiesFromRolesWithoutScopes() {
		long userId = insertUser("metadata-only", "metadata@example.com", "hashedpw", true, false);
		long userRole = insertRole("SYSTEMS");

		linkUserRole(userId, userRole);

		em.flush();
		em.clear();

		UserDetails userDetails = userDetailsService.loadUserByUsername("metadata-only");

		assertThat(userDetails.getAuthorities()).isEmpty();

		var authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		var response = authController.me(authentication, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var json = objectMapper.valueToTree(response.getBody());
		assertThat(json.path("scopes").isEmpty()).isTrue();
		assertThat(json.path("roles").get(0).asText()).isEqualTo("SYSTEMS");
		assertThat(json.path("capabilities").path("systems").path("canRead").asBoolean()).isFalse();
		assertThat(json.path("capabilities").path("systems").path("canWrite").asBoolean()).isFalse();
	}

	private long insertUser(String username, String email, String password, boolean enabled, boolean isAdmin) {
		return ((Number) em.createNativeQuery("""
				INSERT INTO users (username, email, password, enabled, is_admin)
				VALUES (:u, :e, :p, :en, :adm)
				RETURNING id_user
				""").setParameter("u", username).setParameter("e", email).setParameter("p", password)
				.setParameter("en", enabled).setParameter("adm", isAdmin).getSingleResult()).longValue();
	}

	private long insertRole(String name) {
		return ((Number) em.createNativeQuery("""
				INSERT INTO roles (name) VALUES (:n)
				RETURNING id_role
				""").setParameter("n", name).getSingleResult()).longValue();
	}

	private long insertScope(String name) {
		return ((Number) em.createNativeQuery("""
				INSERT INTO scopes (name) VALUES (:n)
				RETURNING id_scope
				""").setParameter("n", name).getSingleResult()).longValue();
	}

	private void linkUserRole(long userId, long roleId) {
		em.createNativeQuery("""
				INSERT INTO users_roles (id_user, id_role)
				VALUES (:u, :r)
				""").setParameter("u", userId).setParameter("r", roleId).executeUpdate();
	}

	private void linkRoleScope(long roleId, long scopeId) {
		em.createNativeQuery("""
				INSERT INTO role_scopes (id_role, id_scope)
				VALUES (:r, :s)
				""").setParameter("r", roleId).setParameter("s", scopeId).executeUpdate();
	}

}

