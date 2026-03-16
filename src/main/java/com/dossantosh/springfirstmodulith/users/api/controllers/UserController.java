package com.dossantosh.springfirstmodulith.users.api.controllers;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.application.services.UserQueryService;
import com.dossantosh.springfirstmodulith.users.application.views.UserDetailsView;
import com.dossantosh.springfirstmodulith.users.application.views.UserSummaryView;

/**
 * REST controller for managing user-related operations.
 *
 * <p>
 * This controller provides endpoints to:
 * <ul>
 * <li>Retrieve a paginated list of users using keyset pagination</li>
 * <li>Get the full details of a specific user by ID</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MODULE_USERS')")
@RequestMapping("/api/users")
public class UserController {

    private final UserQueryService userQueryService;

    /**
     * Retrieves a list of users using keyset pagination, with optional filtering by
     * ID, username, or email.
     *
     * @param id        (optional) Exact user ID to filter by
     * @param username  (optional) Username starts with (case-insensitive)
     * @param email     (optional) Email starts with (case-insensitive)
     * @param lastId    (optional) Last loaded ID for keyset pagination
     * @param limit     Maximum number of results to return (default: 50)
     * @param direction Pagination direction: "NEXT" or "PREVIOUS" (default: NEXT)
     * @return {@link ResponseEntity} containing a {@link KeysetPage} of
     *         {@link UserSummaryView},
     *         or 400 Bad Request if direction is invalid, or 500 Internal Server
     *         Error if service fails
     */
    @GetMapping
    public ResponseEntity<KeysetPage<UserSummaryView>> getUsers(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "25") int limit,
            @RequestParam(defaultValue = "NEXT") String direction) {

        Direction dir;
        try {
            dir = Direction.valueOf(direction.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        KeysetPage<UserSummaryView> users = userQueryService.findUsersKeyset(
                id,
                username != null ? username.toLowerCase() : null,
                email != null ? email.toLowerCase() : null,
                lastId,
                limit,
                dir);

        if (users == null) {
            return ResponseEntity.status(500).body(null); 
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsView> getUserDetails(@PathVariable Long id) {

        return ResponseEntity.ok(userQueryService.getUserDetails(id));
    }
}
