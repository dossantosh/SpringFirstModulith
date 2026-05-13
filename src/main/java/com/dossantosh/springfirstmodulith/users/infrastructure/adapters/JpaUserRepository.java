package com.dossantosh.springfirstmodulith.users.infrastructure.adapters;

import com.dossantosh.springfirstmodulith.users.application.ports.out.UserRepository;
import com.dossantosh.springfirstmodulith.users.domain.entities.User;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.SpringDataUserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class JpaUserRepository implements UserRepository {

	private final SpringDataUserJpaRepository springDataUserJpaRepository;

	JpaUserRepository(SpringDataUserJpaRepository springDataUserJpaRepository) {
		this.springDataUserJpaRepository = springDataUserJpaRepository;
	}

	@Override
	public Optional<User> findById(Long id) {
		return springDataUserJpaRepository.findById(id);
	}

	@Override
	public boolean existsById(Long id) {
		return springDataUserJpaRepository.existsById(id);
	}

	@Override
	public void deleteById(Long id) {
		springDataUserJpaRepository.deleteById(id);
	}

	@Override
	public User save(User user) {
		return springDataUserJpaRepository.save(user);
	}

	@Override
	public List<UserSummaryRow> findUsersKeyset(Long id, String username, String email, Long lastId, int limit,
			String direction) {
		return springDataUserJpaRepository.findUsersKeyset(id, username, email, lastId, limit, direction).stream()
				.map(JpaUserRepository::toSummaryRow).toList();
	}

	@Override
	public Optional<User> findFullUserById(Long id) {
		return springDataUserJpaRepository.findFullUserById(id);
	}

	@Override
	public boolean usernameExists(String username) {
		return springDataUserJpaRepository.existsByUsername(username);
	}

	@Override
	public boolean emailExists(String email) {
		return springDataUserJpaRepository.existsByEmail(email);
	}

	private static UserSummaryRow toSummaryRow(UserProjection projection) {
		return new UserSummaryRow(projection.getId(), projection.getUsername(), projection.getEmail(),
				projection.getEnabled(), projection.getIsAdmin());
	}
}
