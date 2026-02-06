package com.dossantosh.springfirstmodulith.users;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserAuthProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;

@DataJpaTest
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///testdb",
                "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
                "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserRepositoryTest {

        @Autowired
        private UserRepository userRepository;

        @PersistenceContext
        private EntityManager em;

        @Test
        void findUserAuthByUsername_returnsEmpty_whenUserNotFound() {
                Optional<UserAuthProjection> res = userRepository.findUserAuthByUsername("nope");
                assertThat(res).isEmpty();
        }

        @Test
        void findUserAuthByUsername_returnsAggregatedAuthData() {
                // given
                long userId = insertUser("john", "john@x.com", "hashedpw", true, false);

                long roleDoctor = insertRole("DOCTOR");
                long roleMedic = insertRole("MEDIC");
                linkUserRole(userId, roleDoctor);
                linkUserRole(userId, roleMedic);

                long modUsers = insertModule("USERS");
                long modBilling = insertModule("BILLING");
                linkUserModule(userId, modUsers);
                linkUserModule(userId, modBilling);

                long subCreate = insertSubmodule("CREATE", modUsers);
                long subRead = insertSubmodule("READ", modUsers);
                linkUserSubmodule(userId, subCreate);
                linkUserSubmodule(userId, subRead);

                em.flush();
                em.clear();

                // when
                UserAuthProjection p = userRepository.findUserAuthByUsername("john").orElseThrow();

                // then
                assertThat(p.getId()).isEqualTo(userId);
                assertThat(p.getUsername()).isEqualTo("john");
                assertThat(p.getEmail()).isEqualTo("john@x.com");
                assertThat(p.getPassword()).isEqualTo("hashedpw");
                assertThat(p.getEnabled()).isTrue();
                assertThat(p.getIsAdmin()).isFalse();

                assertThat(p.getRoles())
                                .containsExactlyInAnyOrder("DOCTOR", "MEDIC");

                assertThat(p.getModules())
                                .containsExactlyInAnyOrder("USERS", "BILLING");

                assertThat(p.getSubmodules())
                                .containsExactlyInAnyOrder("USERS_CREATE", "USERS_READ");
        }

        @Test
        void findUserAuthByUsername_returnsEmptyCollections_whenUserHasNoRelations() {
                long userId = insertUser("solo", "solo@x.com", "pw", true, false);
                em.flush();
                em.clear();

                UserAuthProjection p = userRepository.findUserAuthByUsername("solo").orElseThrow();

                assertThat(p.getId()).isEqualTo(userId);

                assertThat(p.getRoles()).isNull();
                assertThat(p.getModules()).isNull();
                assertThat(p.getSubmodules()).isNull();

        }

        @Test
        void findUsersKeyset_next_returnsAscending_fromLastId() {
                long u1 = insertUser("a1", "a1@x.com", "pw", true, false);
                insertUser("a2", "a2@x.com", "pw", true, false);
                insertUser("a3", "a3@x.com", "pw", true, false);
                em.flush();
                em.clear();

                var res = userRepository.findUsersKeyset(null, "a", null, u1, 10, "NEXT");
                assertThat(res).extracting(UserProjection::getUsername).containsExactly("a2", "a3");

        }

        private long insertUser(String username, String email, String password, boolean enabled, boolean isAdmin) {
                return ((Number) em.createNativeQuery("""
                                INSERT INTO users (username, email, password, enabled, is_admin)
                                VALUES (:u, :e, :p, :en, :adm)
                                RETURNING id_user
                                """)
                                .setParameter("u", username)
                                .setParameter("e", email)
                                .setParameter("p", password)
                                .setParameter("en", enabled)
                                .setParameter("adm", isAdmin)
                                .getSingleResult()).longValue();
        }

        private long insertRole(String name) {
                return ((Number) em.createNativeQuery("""
                                INSERT INTO roles (name) VALUES (:n)
                                RETURNING id_role
                                """)
                                .setParameter("n", name)
                                .getSingleResult()).longValue();
        }

        private void linkUserRole(long userId, long roleId) {
                em.createNativeQuery("""
                                INSERT INTO users_roles (id_user, id_role)
                                VALUES (:u, :r)
                                """)
                                .setParameter("u", userId)
                                .setParameter("r", roleId)
                                .executeUpdate();
        }

        private long insertModule(String name) {
                return ((Number) em.createNativeQuery("""
                                INSERT INTO modules (name) VALUES (:n)
                                RETURNING id_module
                                """)
                                .setParameter("n", name)
                                .getSingleResult()).longValue();
        }

        private void linkUserModule(long userId, long moduleId) {
                em.createNativeQuery("""
                                INSERT INTO users_modules (id_user, id_module)
                                VALUES (:u, :m)
                                """)
                                .setParameter("u", userId)
                                .setParameter("m", moduleId)
                                .executeUpdate();
        }

        private long insertSubmodule(String name, long moduleId) {
                return ((Number) em.createNativeQuery("""
                                INSERT INTO submodules (name, id_module)
                                VALUES (:n, :m)
                                RETURNING id_submodule
                                """)
                                .setParameter("n", name)
                                .setParameter("m", moduleId)
                                .getSingleResult()).longValue();
        }

        private void linkUserSubmodule(long userId, long submoduleId) {
                em.createNativeQuery("""
                                INSERT INTO users_submodules (id_user, id_submodule)
                                VALUES (:u, :s)
                                """)
                                .setParameter("u", userId)
                                .setParameter("s", submoduleId)
                                .executeUpdate();
        }
}
