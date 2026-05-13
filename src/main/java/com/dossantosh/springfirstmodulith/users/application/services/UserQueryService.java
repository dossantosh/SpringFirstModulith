package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.application.ports.out.RoleRepository;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserRepository;
import com.dossantosh.springfirstmodulith.users.application.views.*;
import com.dossantosh.springfirstmodulith.users.domain.entities.Roles;
import com.dossantosh.springfirstmodulith.users.domain.entities.User;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class UserQueryService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public UserQueryService(UserRepository userRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	public KeysetPage<UserSummaryView> findUsersKeyset(Long id, String username, String email, Long lastId, int limit,
			Direction direction) {
		var rows = userRepository.findUsersKeyset(id, username, email, lastId, limit + 1, direction.name());

		return KeysetPage.fromSlice(rows, limit, direction, lastId, this::toUserSummaryView, UserSummaryView::id);
	}

	public UserDetailsView getUserDetails(Long id) {
		return userRepository.findFullUserById(id).map(this::toUserDetailsView).orElse(null);
	}

	public UserRolesView getUserRoles(Long id) {
		return userRepository.findFullUserById(id).map(this::toUserRolesView).orElse(null);
	}

	private UserSummaryView toUserSummaryView(UserRepository.UserSummaryRow userRow) {
		if (userRow == null) {
			return null;
		}

		return new UserSummaryView(userRow.id(), userRow.username(), userRow.email(), userRow.enabled(),
				userRow.isAdmin());
	}

	private UserDetailsView toUserDetailsView(User user) {
		if (user == null) {
			return null;
		}

		LinkedHashSet<RoleView> roleViews = new LinkedHashSet<>();
		for (Roles role : user.roles()) {
			roleViews.add(new RoleView(role.id(), role.name()));
		}

		return new UserDetailsView(user.id(), user.username(), user.email(), user.enabled(), user.isAdmin(), roleViews);
	}

	private UserRolesView toUserRolesView(User user) {
		LinkedHashSet<RoleView> roleViews = toRoleViews(
				user.roles().stream().sorted(Comparator.comparing(Roles::name)).toList());
		List<RoleView> availableRoles = toRoleViews(roleRepository.findAllRoles()).stream().toList();

		return new UserRolesView(user.id(), user.username(), roleViews, availableRoles);
	}

	private LinkedHashSet<RoleView> toRoleViews(Iterable<Roles> roles) {
		LinkedHashSet<RoleView> roleViews = new LinkedHashSet<>();
		for (Roles role : roles) {
			roleViews.add(new RoleView(role.id(), role.name()));
		}
		return roleViews;
	}
}
