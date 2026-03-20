package com.dossantosh.springfirstmodulith.users.application.ports.out;

import com.dossantosh.springfirstmodulith.users.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserQueryPort {

	List<UserSummaryRow> findUsersKeyset(Long id, String username, String email, Long lastId, int limit,
			String direction);

	Optional<User> findFullUserById(Long id);

	record UserSummaryRow(Long id, String username, String email, Boolean enabled, Boolean isAdmin) {
	}
}
