package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserQueryPort;
import com.dossantosh.springfirstmodulith.users.application.views.*;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class UserQueryService {

	private final UserQueryPort userQueryPort;

	public UserQueryService(UserQueryPort userQueryPort) {
		this.userQueryPort = userQueryPort;
	}

	public KeysetPage<UserSummaryView> findUsersKeyset(Long id, String username, String email, Long lastId, int limit,
			Direction direction) {
		List<UserQueryPort.UserSummaryRow> userRows = new java.util.ArrayList<>(
				userQueryPort.findUsersKeyset(id, username, email, lastId, limit + 1, direction.name()));

		boolean hasMore = userRows.size() > limit;
		if (hasMore) {
			userRows.remove(userRows.size() - 1);
		}

		if (direction == Direction.PREVIOUS) {
			Collections.reverse(userRows);
		}

		List<UserSummaryView> userSummaries = userRows.stream().map(this::toUserSummaryView).toList();

		Long newNextId = null;
		Long newPreviousId = null;
		boolean hasNext = false;
		boolean hasPrevious = false;

		if (!userSummaries.isEmpty()) {
			newNextId = userSummaries.get(userSummaries.size() - 1).id();
			newPreviousId = userSummaries.get(0).id();
			if (direction == Direction.NEXT) {
				hasNext = hasMore;
				hasPrevious = lastId != null;
			} else if (direction == Direction.PREVIOUS) {
				hasNext = lastId != null;
				hasPrevious = hasMore;
			}
		}

		KeysetPage<UserSummaryView> page = new KeysetPage<>();
		page.setContent(userSummaries);
		page.setNextId(newNextId);
		page.setPreviousId(newPreviousId);
		page.setHasNext(hasNext);
		page.setHasPrevious(hasPrevious);

		return page;
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

		LinkedHashSet<ModuleView> moduleViews = new LinkedHashSet<>();
		for (Modules module : user.modules()) {
			moduleViews.add(new ModuleView(module.id(), module.name()));
		}

		LinkedHashSet<SubmoduleView> submoduleViews = new LinkedHashSet<>();
		for (Submodules submodule : user.submodules()) {
			submoduleViews.add(new SubmoduleView(submodule.id(), submodule.name()));
		}

		return new UserDetailsView(user.id(), user.username(), user.email(), user.enabled(), user.isAdmin(), roleViews,
				moduleViews, submoduleViews);
	}
}
