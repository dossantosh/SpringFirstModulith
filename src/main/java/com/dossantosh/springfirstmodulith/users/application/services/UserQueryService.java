package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserQueryPort;
import com.dossantosh.springfirstmodulith.users.application.views.*;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.User;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;

@Service
public class UserQueryService {

	private final UserQueryPort userQueryPort;

	public UserQueryService(UserQueryPort userQueryPort) {
		this.userQueryPort = userQueryPort;
	}

	public KeysetPage<UserSummaryView> findUsersKeyset(Long id, String username, String email, Long lastId, int limit,
			Direction direction) {
		var rows = userQueryPort.findUsersKeyset(id, username, email, lastId, limit + 1, direction.name());

		return KeysetPage.fromSlice(rows, limit, direction, lastId, this::toUserSummaryView, UserSummaryView::id);
	}

	public UserDetailsView getUserDetails(Long id) {
		return userQueryPort.findFullUserById(id).map(this::toUserDetailsView).orElse(null);
	}

	private UserSummaryView toUserSummaryView(UserQueryPort.UserSummaryRow userRow) {
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
}
