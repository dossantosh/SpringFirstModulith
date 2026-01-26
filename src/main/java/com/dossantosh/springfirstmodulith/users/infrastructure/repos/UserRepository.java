package com.dossantosh.springfirstmodulith.users.infrastructure.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dossantosh.springfirstmodulith.users.application.dtos.UserDTO;
import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserAuthProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;

/**
 * Repository interface for managing {@link User} entities.
 * 
 * Provides methods for retrieving users by username, email, and ID,
 * checking existence, and retrieving detailed authentication and user data
 * with custom queries and keyset pagination.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their exact username.
     *
     * @param username the username to search for
     * @return an Optional containing the found User, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their exact email address.
     *
     * @param email the email to search for
     * @return an Optional containing the found User, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists by the given ID.
     *
     * @param id the user ID to check
     * @return true if a user with the ID exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Checks if a user exists with the given username.
     *
     * @param username the username to check
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves authentication-related information for a user by username,
     * including roles, modules, and submodules.
     *
     * Uses a native SQL query to aggregate user permissions and details required
     * for authentication.
     *
     * @param username the username to search for
     * @return an Optional containing a {@link UserAuthProjection} with auth info,
     *         or empty if user not found
     */
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
                        SELECT array_agg(CONCAT(m2.name, '_', s.name))
                        FROM submodules s
                        JOIN modules m2 ON m2.id_module = s.id_module
                        JOIN users_submodules us ON s.id_submodule = us.id_submodule
                        WHERE us.id_user = u.id_user
                    ) AS submodules
                FROM users u
                WHERE u.username = :username
            """, nativeQuery = true)
    Optional<UserAuthProjection> findUserAuthByUsername(String username);

    /**
     * Returns a paginated list of users matching optional filters, using keyset
     * pagination.
     *
     * Supports filtering by ID, username prefix, email prefix, pagination cursor
     * (lastId),
     * limit, and direction (NEXT or PREVIOUS).
     *
     * @param id        optional exact user ID to filter
     * @param username  optional username prefix (case-insensitive)
     * @param email     optional email prefix (case-insensitive)
     * @param lastId    the last seen user ID for pagination cursor
     * @param limit     max number of users to return
     * @param direction pagination direction ("NEXT" or "PREVIOUS")
     * @return a list of {@link UserDTO} matching the filters and pagination
     */
    @Query(value = """
                SELECT u.id_user AS id,
                       u.username AS username,
                       u.email AS email,
                       u.enabled AS enabled,
                       u.is_admin AS isAdmin
                FROM users u
                WHERE (:id IS NULL OR u.id_user = :id)
                  AND (:username IS NULL OR LOWER(u.username) LIKE CONCAT(:username, '%'))
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
    List<UserProjection> findUsersKeyset(
            @Param("id") Long id,
            @Param("username") String username,
            @Param("email") String email,
            @Param("lastId") Long lastId,
            @Param("limit") int limit,
            @Param("direction") String direction);

    /**
     * Finds a user by ID including all associated roles, modules, and submodules,
     * using an {@link EntityGraph} to optimize fetching.
     *
     * @param id the ID of the user
     * @return an Optional containing the full {@link User} entity with
     *         associations,
     *         or empty if not found
     */
    @EntityGraph(attributePaths = { "roles", "modules", "submodules" })
    Optional<User> findFullUserById(Long id);
}
