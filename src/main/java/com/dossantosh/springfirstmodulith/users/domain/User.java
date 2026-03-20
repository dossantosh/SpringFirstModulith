package com.dossantosh.springfirstmodulith.users.domain;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.domain.ports.UserUniquenessPolicy;
import jakarta.persistence.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	private Long id;

	@Column(unique = true, length = 60)
	private String username;

	@Column(unique = true, length = 100)
	private String email;

	@Column
	private Boolean enabled;

	@Column(length = 100)
	private String password;

	@Column(name = "is_admin")
	private Boolean isAdmin;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "id_user", referencedColumnName = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_role", referencedColumnName = "id_role"))
	private final Set<Roles> roles = new LinkedHashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "users_modules", joinColumns = @JoinColumn(name = "id_user", referencedColumnName = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_module", referencedColumnName = "id_module"))
	private final Set<Modules> modules = new LinkedHashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "users_submodules", joinColumns = @JoinColumn(name = "id_user", referencedColumnName = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_submodule", referencedColumnName = "id_submodule"))
	private final Set<Submodules> submodules = new LinkedHashSet<>();

	public User() {
	}

	public User(String username, String email, String password, boolean isAdmin) {
		renameTo(username);
		changeEmail(email);
		changePassword(password);
		changeAdministratorStatus(isAdmin);
		activate();
	}

	public static User rehydrate(Long id, String username, String email, Boolean enabled, String password,
			Boolean isAdmin, UserAccess access) {
		User user = new User();
		user.id = id;
		user.renameTo(username);
		user.changeEmail(email);
		user.changePassword(password);
		user.enabled = requireBoolean(enabled, "enabled");
		user.isAdmin = requireBoolean(isAdmin, "isAdmin");

		if (access != null) {
			user.replaceAccess(access);
		}

		return user;
	}

	public Set<Roles> roles() {
		return Collections.unmodifiableSet(roles);
	}

	public Long id() {
		return id;
	}

	public String username() {
		return username;
	}

	public String email() {
		return email;
	}

	public Boolean enabled() {
		return enabled;
	}

	public String passwordHash() {
		return password;
	}

	public Boolean isAdmin() {
		return isAdmin;
	}

	public Set<Modules> modules() {
		return Collections.unmodifiableSet(modules);
	}

	public Set<Submodules> submodules() {
		return Collections.unmodifiableSet(submodules);
	}

	public void renameTo(String username) {
		String normalized = normalizeRequired(username, "username");
		this.username = normalized;
	}

	public void changeEmail(String email) {
		String normalized = normalizeEmailRequired(email);
		this.email = normalized;
	}

	public void changePassword(String password) {
		String normalized = normalizeRequired(password, "password");
		this.password = normalized;
	}

	public void activate() {
		this.enabled = true;
	}

	public void disable() {
		this.enabled = false;
	}

	public void changeAdministratorStatus(boolean admin) {
		this.isAdmin = admin;
	}

	public void prepareForCreation(UserUniquenessPolicy uniquenessPolicy) {
		UserUniquenessPolicy ensuredUniquenessPolicy = requireUniquenessPolicy(uniquenessPolicy);
		normalizeStateForCreation();
		assertUniqueForCreation(ensuredUniquenessPolicy);
	}

	private void normalizeStateForCreation() {
		renameTo(username);
		changeEmail(email);
		changePassword(password);
		changeAdministratorStatus(Boolean.TRUE.equals(isAdmin));
		activate();
		validateAccessStateForCreation();
	}

	public void replaceAccess(UserAccess access) {
		if (access == null) {
			throw new BusinessException("User access cannot be null");
		}

		this.roles.clear();
		this.roles.addAll(access.roles());

		this.modules.clear();
		this.modules.addAll(access.modules());

		this.submodules.clear();
		this.submodules.addAll(access.submodules());
	}

	public void applyChangesFrom(UserChanges changes, UserUniquenessPolicy uniquenessPolicy) {
		if (changes == null) {
			throw new BusinessException("Changes cannot be null");
		}
		UserUniquenessPolicy ensuredUniquenessPolicy = requireUniquenessPolicy(uniquenessPolicy);
		assertUniqueForUpdate(changes, ensuredUniquenessPolicy);

		if (hasText(changes.username())) {
			renameTo(changes.username());
		}

		if (hasText(changes.email())) {
			changeEmail(changes.email());
		}

		if (changes.enabled() != null) {
			if (changes.enabled()) {
				activate();
			} else {
				disable();
			}
		}

		if (hasText(changes.password())) {
			changePassword(changes.password());
		}

		if (changes.isAdmin() != null) {
			changeAdministratorStatus(changes.isAdmin());
		}

		if (changes.access() != null) {
			replaceAccess(changes.access());
		}
	}

	public boolean hasNoAccessAssigned() {
		return roles.isEmpty() && modules.isEmpty() && submodules.isEmpty();
	}

	private static String normalizeRequired(String value, String fieldName) {
		String normalized = normalizeNullable(value);
		if (!hasText(normalized)) {
			throw new BusinessException(fieldName + " cannot be blank");
		}
		return normalized;
	}

	private static String normalizeEmailRequired(String email) {
		String normalized = normalizeEmailNullable(email);
		if (!hasText(normalized)) {
			throw new BusinessException("email cannot be blank");
		}
		return normalized;
	}

	private static String normalizeNullable(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private static String normalizeEmailNullable(String email) {
		String normalized = normalizeNullable(email);
		return normalized == null ? null : normalized.toLowerCase();
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private static boolean requireBoolean(Boolean value, String fieldName) {
		if (value == null) {
			throw new BusinessException(fieldName + " cannot be null");
		}
		return value;
	}

	private void assertUniqueForCreation(UserUniquenessPolicy uniquenessPolicy) {
		if (uniquenessPolicy.usernameExists(username)) {
			throw new BusinessException("Username '" + username + "' is already in use");
		}
		if (uniquenessPolicy.emailExists(email)) {
			throw new BusinessException("Email '" + email + "' is already in use");
		}
	}

	private void assertUniqueForUpdate(UserChanges changes, UserUniquenessPolicy uniquenessPolicy) {
		if (hasText(changes.username())) {
			String candidateUsername = normalizeRequired(changes.username(), "username");
			if (!candidateUsername.equals(username) && uniquenessPolicy.usernameExists(candidateUsername)) {
				throw new BusinessException("Username '" + candidateUsername + "' is already in use");
			}
		}

		if (hasText(changes.email())) {
			String candidateEmail = normalizeEmailRequired(changes.email());
			if (!candidateEmail.equals(email) && uniquenessPolicy.emailExists(candidateEmail)) {
				throw new BusinessException("Email '" + candidateEmail + "' is already in use");
			}
		}
	}

	private static UserUniquenessPolicy requireUniquenessPolicy(UserUniquenessPolicy uniquenessPolicy) {
		if (uniquenessPolicy == null) {
			throw new BusinessException("User uniqueness policy cannot be null");
		}
		return uniquenessPolicy;
	}

	private void validateAccessStateForCreation() {
		boolean hasAnyAccess = !roles.isEmpty() || !modules.isEmpty() || !submodules.isEmpty();
		boolean hasCompleteAccess = !roles.isEmpty() && !modules.isEmpty() && !submodules.isEmpty();

		if (hasAnyAccess && !hasCompleteAccess) {
			throw new BusinessException("User access must be fully specified or omitted during creation");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof User other)) {
			return false;
		}
		return id != null && Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
