package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.users.application.ports.out.UserCommandPort;
import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.domain.UserChanges;
import com.dossantosh.springfirstmodulith.users.domain.ports.UserUniquenessPolicy;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCommandService {

	private final UserCommandPort userCommandPort;
	private final DefaultUserAccessPolicyService defaultUserAccessPolicyService;
	private final UserUniquenessPolicy userUniquenessPolicy;
	private final PasswordEncoder passwordEncoder;

	public UserCommandService(UserCommandPort userCommandPort,
			DefaultUserAccessPolicyService defaultUserAccessPolicyService, UserUniquenessPolicy userUniquenessPolicy,
			PasswordEncoder passwordEncoder) {
		this.userCommandPort = userCommandPort;
		this.defaultUserAccessPolicyService = defaultUserAccessPolicyService;
		this.userUniquenessPolicy = userUniquenessPolicy;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void deleteById(Long id) {
		if (!userCommandPort.existsById(id)) {
			throw new EntityNotFoundException("User with ID " + id + " not found");
		}
		userCommandPort.deleteById(id);
	}

	@Transactional
	public User modifyUser(Long userId, UserChanges changes) {
		User existingUser = userCommandPort.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

		UserChanges normalizedChanges = changes;
		if (hasText(changes.password())) {
			normalizedChanges = new UserChanges(changes.username(), changes.email(), changes.enabled(),
					passwordEncoder.encode(changes.password()), changes.isAdmin(), changes.access());
		}

		existingUser.applyChangesFrom(normalizedChanges, userUniquenessPolicy);
		return save(existingUser);
	}

	@Transactional
	public User createUser(User user) {
		user.prepareForCreation(userUniquenessPolicy);
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
		return userCommandPort.save(user);
	}
}
