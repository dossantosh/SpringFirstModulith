package com.dossantosh.springfirstmodulith.users.api.controllers;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.api.requests.CreateUserRequest;
import com.dossantosh.springfirstmodulith.users.api.requests.UpdateUserRequest;
import com.dossantosh.springfirstmodulith.users.api.requests.UserAccessRequest;
import com.dossantosh.springfirstmodulith.users.application.services.UserAccessResolverService;
import com.dossantosh.springfirstmodulith.users.application.services.UserCommandService;
import com.dossantosh.springfirstmodulith.users.application.services.UserQueryService;
import com.dossantosh.springfirstmodulith.users.application.views.UserDetailsView;
import com.dossantosh.springfirstmodulith.users.application.views.UserSummaryView;
import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import com.dossantosh.springfirstmodulith.users.domain.UserChanges;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAuthority('MODULE_USERS')")
@RequestMapping("/api/users")
public class UserController {

	private final UserCommandService userCommandService;
	private final UserAccessResolverService userAccessResolverService;
	private final UserQueryService userQueryService;

	public UserController(UserCommandService userCommandService, UserAccessResolverService userAccessResolverService,
			UserQueryService userQueryService) {
		this.userCommandService = userCommandService;
		this.userAccessResolverService = userAccessResolverService;
		this.userQueryService = userQueryService;
	}

	@GetMapping
	public ResponseEntity<KeysetPage<UserSummaryView>> getUsers(@RequestParam(required = false) Long id,
			@RequestParam(required = false) String username, @RequestParam(required = false) String email,
			@RequestParam(required = false) Long lastId, @RequestParam(defaultValue = "25") int limit,
			@RequestParam(defaultValue = "NEXT") String direction) {

		Direction dir;
		try {
			dir = Direction.valueOf(direction.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}

		KeysetPage<UserSummaryView> users = userQueryService.findUsersKeyset(id,
				username != null ? username.toLowerCase() : null, email != null ? email.toLowerCase() : null, lastId,
				limit, dir);

		if (users == null) {
			return ResponseEntity.status(500).body(null);
		}

		return ResponseEntity.ok(users);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDetailsView> getUserDetails(@PathVariable Long id) {

		return ResponseEntity.ok(userQueryService.getUserDetails(id));
	}

	@PostMapping
	public ResponseEntity<UserDetailsView> createUser(@Valid @RequestBody CreateUserRequest request) {
		User user = new User(request.username(), request.email(), request.password(), request.isAdmin());
		UserAccess access = toUserAccessOrNull(request.access());
		if (access != null) {
			user.replaceAccess(access);
		}

		User created = userCommandService.createUser(user);
		return ResponseEntity.status(201).body(userQueryService.getUserDetails(created.id()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserDetailsView> updateUser(@PathVariable Long id,
			@Valid @RequestBody UpdateUserRequest request) {
		UserAccess access = toUserAccessOrNull(request.access());
		UserChanges changes = new UserChanges(request.username(), request.email(), request.enabled(),
				request.password(), request.isAdmin(), access);

		User updated = userCommandService.modifyUser(id, changes);
		return ResponseEntity.ok(userQueryService.getUserDetails(updated.id()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userCommandService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	private UserAccess toUserAccessOrNull(UserAccessRequest request) {
		if (request == null) {
			return null;
		}
		return userAccessResolverService.resolve(request.roleIds(), request.moduleIds(), request.submoduleIds());
	}
}
