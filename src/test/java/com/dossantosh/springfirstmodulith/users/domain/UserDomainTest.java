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
	void replaceAccess_rejectsSubmoduleFromUnassignedModule() {
		Modules users = module(1L, "Users");
		Modules perfumes = module(2L, "Perfumes");
		Roles userRole = role(3L, "USER");
		Submodules searchPerfumes = submodule(4L, "SEARCH_PERFUMES", perfumes);

		User user = new User("john", "john@x.com", "secret", false);

		assertThatThrownBy(
				() -> user.replaceAccess(UserAccess.of(Set.of(userRole), Set.of(users), Set.of(searchPerfumes))))
				.isInstanceOf(BusinessException.class).hasMessageContaining("requires its parent module");
	}

	@Test
	void replaceAccess_allowsRoleOnlyAccess() {
		Roles userRole = role(2L, "USER");
		User user = new User("john", "john@x.com", "secret", false);

		user.replaceAccess(UserAccess.of(Set.of(userRole), Set.of(), Set.of()));

		assertThat(user.roles()).containsExactly(userRole);
		assertThat(user.modules()).isEmpty();
		assertThat(user.submodules()).isEmpty();
	}

	@Test
	void applyChangesFrom_updatesBehavioralStateInsideAggregate() {
		Modules users = module(1L, "Users");
		Roles userRole = role(2L, "USER");
		Submodules searchUsers = submodule(3L, "SEARCH_USERS", users);
		UserAccess access = UserAccess.of(Set.of(userRole), Set.of(users), Set.of(searchUsers));

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
		Modules users = module(1L, "Users");
		Roles userRole = role(2L, "USER");
		Submodules searchUsers = submodule(3L, "SEARCH_USERS", users);

		assertThat(user.hasNoAccessAssigned()).isTrue();

		user.replaceAccess(UserAccess.of(Set.of(userRole), Set.of(users), Set.of(searchUsers)));

		assertThat(user.hasNoAccessAssigned()).isFalse();
	}

	@Test
	void applyChangesFrom_whenAccessIsNull_keepsExistingAccess() {
		Modules users = module(1L, "Users");
		Roles userRole = role(2L, "USER");
		Submodules searchUsers = submodule(3L, "SEARCH_USERS", users);
		UserAccess access = UserAccess.of(Set.of(userRole), Set.of(users), Set.of(searchUsers));

		User existing = new User("john", "john@x.com", "secret", false);
		existing.replaceAccess(access);

		existing.applyChangesFrom(new UserChanges(null, "john+updated@x.com", null, null, null, null),
				UserUniquenessPolicy.PERMISSIVE);

		assertThat(existing.email()).isEqualTo("john+updated@x.com");
		assertThat(existing.roles()).containsExactly(userRole);
		assertThat(existing.modules()).containsExactly(users);
		assertThat(existing.submodules()).containsExactly(searchUsers);
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

	private static Modules module(Long id, String name) {
		return Modules.reference(id, name);
	}

	private static Submodules submodule(Long id, String name, Modules module) {
		return Submodules.reference(id, name, module);
	}
}
