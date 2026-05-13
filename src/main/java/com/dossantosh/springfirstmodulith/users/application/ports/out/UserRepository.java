package com.dossantosh.springfirstmodulith.users.application.ports.out;

import com.dossantosh.springfirstmodulith.users.domain.entities.User;
import com.dossantosh.springfirstmodulith.users.domain.ports.UserUniquenessPolicy;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends UserUniquenessPolicy {

	Optional<User> findById(Long id);

	boolean existsById(Long id);

	void deleteById(Long id);

	User save(User user);

	List<UserSummaryRow> findUsersKeyset(Long id, String username, String email, Long lastId, int limit,
			String direction);

	Optional<User> findFullUserById(Long id);

	record UserSummaryRow(Long id, String username, String email, Boolean enabled, Boolean isAdmin) {
	}
}
