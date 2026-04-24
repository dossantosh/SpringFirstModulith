package com.dossantosh.springfirstmodulith.users.infrastructure.repos;

import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserAuthProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Override
	@EntityGraph(attributePaths = {"roles", "modules", "submodules"})
	Optional<User> findById(Long id);

	boolean existsById(Long id);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	@Query(value = """
			    SELECT
			        u.id_user AS id,
			        u.username AS username,
			        u.email AS email,
			        u.password AS password,
			        u.enabled AS enabled,
			        u.is_admin AS isAdmin,
			        (
			            SELECT array_agg(r.name)
			            FROM roles r
			            JOIN users_roles ur ON r.id_role = ur.id_role
			            WHERE ur.id_user = u.id_user
			        ) AS roles,
			        (
			            SELECT array_agg(m.name)
			            FROM modules m
			            JOIN users_modules um ON m.id_module = um.id_module
			            WHERE um.id_user = u.id_user
			        ) AS modules,
			        (
			            SELECT array_agg(s.name)
			            FROM submodules s
			            JOIN users_submodules us ON s.id_submodule = us.id_submodule
			            WHERE us.id_user = u.id_user
			        ) AS submodules
			    FROM users u
			    WHERE u.username = :username
			""", nativeQuery = true)
	Optional<UserAuthProjection> findUserAuthByUsername(String username);

	@Query(value = """
			    SELECT u.id_user AS id,
			           u.username AS username,
			           u.email AS email,
			           u.enabled AS enabled,
			           u.is_admin AS isAdmin
			    FROM users u
			    WHERE (:id IS NULL OR u.id_user = :id)
			      AND (:username IS NULL OR LOWER(u.username) LIKE CONCAT(LOWER(:username), '%'))
			      AND (:email IS NULL OR LOWER(u.email) LIKE CONCAT(:email, '%'))
			      AND (
			          (:direction = 'NEXT' AND (:lastId IS NULL OR u.id_user > :lastId))
			          OR
			          (:direction = 'PREVIOUS' AND (:lastId IS NULL OR u.id_user < :lastId))
			      )
			    ORDER BY
			      CASE WHEN :direction = 'NEXT' THEN u.id_user END ASC,
			      CASE WHEN :direction = 'PREVIOUS' THEN u.id_user END DESC
			    LIMIT :limit
			""", nativeQuery = true)
	List<UserProjection> findUsersKeyset(@Param("id") Long id, @Param("username") String username,
			@Param("email") String email, @Param("lastId") Long lastId, @Param("limit") int limit,
			@Param("direction") String direction);

	@EntityGraph(attributePaths = {"roles", "modules", "submodules"})
	Optional<User> findFullUserById(Long id);
}
