package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.users.application.ports.out.UserRepository;
import com.dossantosh.springfirstmodulith.users.domain.entities.User;
import com.dossantosh.springfirstmodulith.users.domain.UserChanges;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCommandService {

	private final UserRepository userRepository;
	private final DefaultUserAccessPolicyService defaultUserAccessPolicyService;
	private final PasswordEncoder passwordEncoder;

	public UserCommandService(UserRepository userRepository,
			DefaultUserAccessPolicyService defaultUserAccessPolicyService, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.defaultUserAccessPolicyService = defaultUserAccessPolicyService;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void deleteById(Long id) {
		if (!userRepository.existsById(id)) {
			throw new EntityNotFoundException("User with ID " + id + " not found");
		}
		userRepository.deleteById(id);
	}

	@Transactional
	public User modifyUser(Long userId, UserChanges changes) {
		User existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		UserChanges normalizedChanges = changes;
		if (hasText(changes.password())) {
			normalizedChanges = new UserChanges(changes.username(), changes.email(), changes.enabled(),
					passwordEncoder.encode(changes.password()), changes.isAdmin(), changes.access());
		}

		existingUser.applyChangesFrom(normalizedChanges, userRepository);
		return save(existingUser);
	}

	@Transactional
	public User createUser(User user) {
		user.prepareForCreation(userRepository);
		if (user.hasNoAccessAssigned()) {
			user.replaceAccess(defaultUserAccessPolicyService.defaultAccessForNewUser());
		}
		user.changePassword(passwordEncoder.encode(user.passwordHash()));
		return save(user);
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private User save(User user) {
		return userRepository.save(user);
	}
}
