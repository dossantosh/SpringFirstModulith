package com.dossantosh.springfirstmodulith.users.domain;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.domain.*;
import com.dossantosh.springfirstmodulith.users.domain.ports.UserUniquenessPolicy;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserDomainTest {

	@Test
	void replaceAccess_allowsRoleOnlyAccess() {
		Roles userRole = role(2L, "USER");
		User user = new User("john", "john@x.com", "secret", false);

		user.replaceAccess(UserAccess.of(Set.of(userRole)));

		assertThat(user.roles()).containsExactly(userRole);
	}

	@Test
	void applyChangesFrom_updatesBehavioralStateInsideAggregate() {
		Roles userRole = role(2L, "USER");
		UserAccess access = UserAccess.of(Set.of(userRole));

		User existing = new User("john", "john@x.com", "secret", false);
		existing.replaceAccess(access);

		UserChanges changes = new UserChanges(null, "john+updated@x.com", false, null, true, access);

		existing.applyChangesFrom(changes, UserUniquenessPolicy.PERMISSIVE);

		assertThat(existing.email()).isEqualTo("john+updated@x.com");
		assertThat(existing.enabled()).isFalse();
		assertThat(existing.isAdmin()).isTrue();
		assertThat(existing.roles()).containsExactly(userRole);
	}

	@Test
	void hasNoAccessAssigned_reflectsWhetherAccessWasConfigured() {
		User user = new User("john", "john@x.com", "secret", false);
		Roles userRole = role(2L, "USER");

		assertThat(user.hasNoAccessAssigned()).isTrue();

		user.replaceAccess(UserAccess.of(Set.of(userRole)));

		assertThat(user.hasNoAccessAssigned()).isFalse();
	}

	@Test
	void applyChangesFrom_whenAccessIsNull_keepsExistingAccess() {
		Roles userRole = role(2L, "USER");
		UserAccess access = UserAccess.of(Set.of(userRole));

		User existing = new User("john", "john@x.com", "secret", false);
		existing.replaceAccess(access);

		existing.applyChangesFrom(new UserChanges(null, "john+updated@x.com", null, null, null, null),
				UserUniquenessPolicy.PERMISSIVE);

		assertThat(existing.email()).isEqualTo("john+updated@x.com");
		assertThat(existing.roles()).containsExactly(userRole);
	}

	@Test
	void prepareForCreation_whenUsernameExists_rejectsAtAggregateLevel() {
		User user = new User("john", "john@x.com", "secret", false);

		UserUniquenessPolicy uniquenessPolicy = new UserUniquenessPolicy() {
			@Override
			public boolean usernameExists(String username) {
				return "john".equals(username);
			}

			@Override
			public boolean emailExists(String email) {
				return false;
			}
		};

		assertThatThrownBy(() -> user.prepareForCreation(uniquenessPolicy)).isInstanceOf(BusinessException.class)
				.hasMessageContaining("Username 'john' is already in use");
	}

	@Test
	void applyChangesFrom_whenEmailExists_rejectsAtAggregateLevel() {
		User existing = new User("john", "john@x.com", "secret", false);

		UserUniquenessPolicy uniquenessPolicy = new UserUniquenessPolicy() {
			@Override
			public boolean usernameExists(String username) {
				return false;
			}

			@Override
			public boolean emailExists(String email) {
				return "john+updated@x.com".equals(email);
			}
		};

		assertThatThrownBy(
				() -> existing.applyChangesFrom(new UserChanges(null, "john+updated@x.com", null, null, null, null),
						uniquenessPolicy))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining("Email 'john+updated@x.com' is already in use");
	}

	private static Roles role(Long id, String name) {
		return Roles.reference(id, name);
	}
}

