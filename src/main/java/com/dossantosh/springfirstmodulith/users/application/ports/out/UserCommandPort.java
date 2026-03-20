package com.dossantosh.springfirstmodulith.users.application.ports.out;

import com.dossantosh.springfirstmodulith.users.domain.User;

import java.util.Optional;

public interface UserCommandPort {

	Optional<User> findById(Long id);

	boolean existsById(Long id);

	void deleteById(Long id);

	User save(User user);
}
