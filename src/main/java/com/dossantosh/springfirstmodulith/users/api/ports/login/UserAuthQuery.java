package com.dossantosh.springfirstmodulith.users.api.ports.login;

import java.util.Optional;

/**
 * Provides read-only authentication and authorization data for a user.
 *
 * This interface is part of the users module public API and must not expose
 * persistence-specific details (JPA entities, Spring Data projections, etc.).
 */
public interface UserAuthQuery {

    /**
     * Retrieves authentication and authorization data for the given username.
     *
     * @param username the username to search for
     * @return an Optional containing the user auth view, or empty if not found
     */
    Optional<UserAuthView> findByUsername(String username);
}
