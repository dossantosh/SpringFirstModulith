package com.dossantosh.springfirstmodulith.users.infrastructure.repos;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserAuthProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {"spring.datasource.url=jdbc:tc:postgresql:17-alpine:///testdb",
		"spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
		"spring.jpa.hibernate.ddl-auto=create-drop"})
class SpringDataUserJpaRepositoryTest {

	@Autowired
	private SpringDataUserJpaRepository springDataUserJpaRepository;

	@PersistenceContext
	private EntityManager em;

	@Test
	void findUserAuthByUsername_returnsEmpty_whenUserNotFound() {
		Optional<UserAuthProjection> res = springDataUserJpaRepository.findUserAuthByUsername("nope");
		assertThat(res).isEmpty();
	}

	@Test
	void findUserAuthByUsername_returnsAggregatedAuthData() {

		long userId = insertUser("john", "john@x.com", "hashedpw", true, false);

		long roleDoctor = insertRole("DOCTOR");
		long roleMedic = insertRole("MEDIC");
		linkUserRole(userId, roleDoctor);
		linkUserRole(userId, roleMedic);
		long userReadScope = insertScope(AuthorizationScopes.SYSTEMS_READ);
		long userCreateScope = insertScope(AuthorizationScopes.SYSTEMS_WRITE);
		linkRoleScope(roleDoctor, userReadScope);
		linkRoleScope(roleMedic, userCreateScope);

		em.flush();
		em.clear();

		UserAuthProjection p = springDataUserJpaRepository.findUserAuthByUsername("john").orElseThrow();

		assertThat(p.getId()).isEqualTo(userId);
		assertThat(p.getUsername()).isEqualTo("john");
		assertThat(p.getEmail()).isEqualTo("john@x.com");
		assertThat(p.getPassword()).isEqualTo("hashedpw");
		assertThat(p.getEnabled()).isTrue();
		assertThat(p.getIsAdmin()).isFalse();

		assertThat(p.getRoles()).containsExactlyInAnyOrder("DOCTOR", "MEDIC");
		assertThat(p.getScopes()).containsExactly(AuthorizationScopes.SYSTEMS_READ, AuthorizationScopes.SYSTEMS_WRITE);
	}

	@Test
	void findUserAuthByUsername_returnsEmptyCollections_whenUserHasNoRelations() {
		long userId = insertUser("solo", "solo@x.com", "pw", true, false);
		em.flush();
		em.clear();

		UserAuthProjection p = springDataUserJpaRepository.findUserAuthByUsername("solo").orElseThrow();

		assertThat(p.getId()).isEqualTo(userId);

		assertThat(p.getRoles()).isNull();
		assertThat(p.getScopes()).isNull();

	}

	@Test
	void findUsersKeyset_next_returnsAscending_fromLastId() {
		long u1 = insertUser("a1", "a1@x.com", "pw", true, false);
		insertUser("a2", "a2@x.com", "pw", true, false);
		insertUser("a3", "a3@x.com", "pw", true, false);
		em.flush();
		em.clear();

		var res = springDataUserJpaRepository.findUsersKeyset(null, "a", null, u1, 10, "NEXT");
		assertThat(res).extracting(UserProjection::getUsername).containsExactly("a2", "a3");

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

	private void linkUserRole(long userId, long roleId) {
		em.createNativeQuery("""
				INSERT INTO users_roles (id_user, id_role)
				VALUES (:u, :r)
				""").setParameter("u", userId).setParameter("r", roleId).executeUpdate();
	}

	private long insertScope(String name) {
		return ((Number) em.createNativeQuery("""
				INSERT INTO scopes (name) VALUES (:n)
				RETURNING id_scope
				""").setParameter("n", name).getSingleResult()).longValue();
	}

	private void linkRoleScope(long roleId, long scopeId) {
		em.createNativeQuery("""
				INSERT INTO role_scopes (id_role, id_scope)
				VALUES (:r, :s)
				""").setParameter("r", roleId).setParameter("s", scopeId).executeUpdate();
	}

}
