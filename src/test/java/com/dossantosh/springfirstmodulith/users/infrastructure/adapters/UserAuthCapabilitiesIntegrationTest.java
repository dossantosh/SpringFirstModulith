package com.dossantosh.springfirstmodulith.users.infrastructure.adapters;

import com.dossantosh.springfirstmodulith.security.SecurityAuthorityNames;
import com.dossantosh.springfirstmodulith.security.api.AuthController;
import com.dossantosh.springfirstmodulith.security.login.CustomUserDetailsService;
import com.dossantosh.springfirstmodulith.security.session.CurrentSessionDataViewProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaUserAdapter.class, CustomUserDetailsService.class, CurrentSessionDataViewProvider.class,
		AuthController.class})
@TestPropertySource(properties = {"spring.datasource.url=jdbc:tc:postgresql:16-alpine:///testdb",
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

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void me_mapsDatabaseAccessCatalogToSemanticCapabilities() {
		long userId = insertUser("john", "john@example.com", "hashedpw", true, false);
		long userRole = insertRole("USER");
		long usersModule = insertModule("USERS");
		long perfumesModule = insertModule("PERFUMES");
		long readUsers = insertSubmodule("READUSERS", usersModule);
		long writeUsers = insertSubmodule("WRITEUSERS", usersModule);
		long readPerfumes = insertSubmodule("READPERFUMES", perfumesModule);
		long writePerfumes = insertSubmodule("WRITEPERFUMES", perfumesModule);

		linkUserRole(userId, userRole);
		linkUserModule(userId, usersModule);
		linkUserModule(userId, perfumesModule);
		linkUserSubmodule(userId, readUsers);
		linkUserSubmodule(userId, writeUsers);
		linkUserSubmodule(userId, readPerfumes);
		linkUserSubmodule(userId, writePerfumes);

		em.flush();
		em.clear();

		UserDetails userDetails = userDetailsService.loadUserByUsername("john");

		assertThat(userDetails.getAuthorities()).extracting("authority")
			.containsExactlyInAnyOrder(SecurityAuthorityNames.ROLE_USER, SecurityAuthorityNames.MODULE_USERS,
					SecurityAuthorityNames.MODULE_PERFUMES, SecurityAuthorityNames.SUBMODULE_READ_USERS,
					SecurityAuthorityNames.SUBMODULE_WRITE_USERS, SecurityAuthorityNames.SUBMODULE_READ_PERFUMES,
					SecurityAuthorityNames.SUBMODULE_WRITE_PERFUMES);

		var authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		var response = authController.me(authentication, new MockHttpSession());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var json = objectMapper.valueToTree(response.getBody());
		assertThat(json.has("authorities")).isFalse();
		assertThat(json.path("username").asText()).isEqualTo("john");
		assertThat(json.path("capabilities").path("users").path("access").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("users").path("read").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("users").path("write").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("perfumes").path("access").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("perfumes").path("read").asBoolean()).isTrue();
		assertThat(json.path("capabilities").path("perfumes").path("write").asBoolean()).isTrue();
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

	private long insertModule(String name) {
		return ((Number) em.createNativeQuery("""
				INSERT INTO modules (name) VALUES (:n)
				RETURNING id_module
				""").setParameter("n", name).getSingleResult()).longValue();
	}

	private long insertSubmodule(String name, long moduleId) {
		return ((Number) em.createNativeQuery("""
				INSERT INTO submodules (name, id_module)
				VALUES (:n, :m)
				RETURNING id_submodule
				""").setParameter("n", name).setParameter("m", moduleId).getSingleResult()).longValue();
	}

	private void linkUserRole(long userId, long roleId) {
		em.createNativeQuery("""
				INSERT INTO users_roles (id_user, id_role)
				VALUES (:u, :r)
				""").setParameter("u", userId).setParameter("r", roleId).executeUpdate();
	}

	private void linkUserModule(long userId, long moduleId) {
		em.createNativeQuery("""
				INSERT INTO users_modules (id_user, id_module)
				VALUES (:u, :m)
				""").setParameter("u", userId).setParameter("m", moduleId).executeUpdate();
	}

	private void linkUserSubmodule(long userId, long submoduleId) {
		em.createNativeQuery("""
				INSERT INTO users_submodules (id_user, id_submodule)
				VALUES (:u, :s)
				""").setParameter("u", userId).setParameter("s", submoduleId).executeUpdate();
	}
}
