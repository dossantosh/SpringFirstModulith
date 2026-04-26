package com.dossantosh.springfirstmodulith.users.infrastructure.adapters;

import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthQuery;
import com.dossantosh.springfirstmodulith.users.api.ports.login.UserAuthView;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserCommandPort;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserQueryPort;
import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.domain.ports.UserUniquenessPolicy;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserAuthProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
class JpaUserAdapter implements UserCommandPort, UserQueryPort, UserUniquenessPolicy, UserAuthQuery {

	private final UserRepository userRepository;

	JpaUserAdapter(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public boolean existsById(Long id) {
		return userRepository.existsById(id);
	}

	@Override
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public List<UserSummaryRow> findUsersKeyset(Long id, String username, String email, Long lastId, int limit,
			String direction) {
		return userRepository.findUsersKeyset(id, username, email, lastId, limit, direction).stream()
				.map(JpaUserAdapter::toSummaryRow).toList();
	}

	@Override
	public Optional<User> findFullUserById(Long id) {
		return userRepository.findFullUserById(id);
	}

	@Override
	public boolean usernameExists(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public boolean emailExists(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public Optional<UserAuthView> findByUsername(String username) {
		return userRepository.findUserAuthByUsername(username).map(this::toAuthView);
	}

	private static UserSummaryRow toSummaryRow(UserProjection projection) {
		return new UserSummaryRow(projection.getId(), projection.getUsername(), projection.getEmail(),
				projection.getEnabled(), projection.getIsAdmin());
	}

	private UserAuthView toAuthView(UserAuthProjection projection) {
		return new UserAuthView(projection.getId(), projection.getUsername(), projection.getEmail(),
				projection.getPassword(), Boolean.TRUE.equals(projection.getEnabled()),
				Boolean.TRUE.equals(projection.getIsAdmin()), emptyIfNull(projection.getRoles()),
				emptyIfNull(projection.getModules()), emptyIfNull(projection.getSubmodules()),
				emptyIfNull(projection.getScopes()));
	}

	private List<String> emptyIfNull(List<String> values) {
		return values == null ? Collections.emptyList() : values;
	}
}
