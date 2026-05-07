package com.dossantosh.springfirstmodulith.users.infrastructure.adapters;

import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthQuery;
import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthView;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserAuthProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
class JpaUserAuthAdapter implements UserAuthQuery {

	private final UserRepository userRepository;

	JpaUserAuthAdapter(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Optional<UserAuthView> findByUsername(String username) {
		return userRepository.findUserAuthByUsername(username).map(JpaUserAuthAdapter::toAuthView);
	}

	private static UserAuthView toAuthView(UserAuthProjection projection) {
		return new UserAuthView(projection.getId(), projection.getUsername(), projection.getEmail(),
				projection.getPassword(), Boolean.TRUE.equals(projection.getEnabled()),
				Boolean.TRUE.equals(projection.getIsAdmin()), emptyIfNull(projection.getRoles()),
				emptyIfNull(projection.getScopes()));
	}

	private static List<String> emptyIfNull(List<String> values) {
		return values == null ? Collections.emptyList() : values;
	}
}
