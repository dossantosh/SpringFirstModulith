package com.dossantosh.springfirstmodulith.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.dossantosh.springfirstmodulith.core.errors.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.domain.Modules;
import com.dossantosh.springfirstmodulith.users.domain.Roles;
import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;

class UserDomainTest {

    @Test
    void replaceAccess_rejectsSubmoduleFromUnassignedModule() {
        Modules users = module(1L, "Users");
        Modules perfumes = module(2L, "Perfumes");
        Roles userRole = role(3L, "USER");
        Submodules writePerfumes = submodule(4L, "WritePerfumes", perfumes);

        User user = new User("john", "john@x.com", "secret", false);

        assertThatThrownBy(() -> user.replaceAccess(UserAccess.of(
                Set.of(userRole),
                Set.of(users),
                Set.of(writePerfumes))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("requires its parent module");
    }

    @Test
    void applyChangesFrom_updatesBehavioralStateInsideAggregate() {
        Modules users = module(1L, "Users");
        Roles userRole = role(2L, "USER");
        Submodules readUsers = submodule(3L, "ReadUsers", users);

        User existing = new User("john", "john@x.com", "secret", false);
        existing.replaceAccess(UserAccess.of(Set.of(userRole), Set.of(users), Set.of(readUsers)));

        User changes = new User();
        changes.setEmail("john+updated@x.com");
        changes.setEnabled(false);
        changes.setIsAdmin(true);

        existing.applyChangesFrom(changes, UserAccess.of(Set.of(userRole), Set.of(users), Set.of(readUsers)));

        assertThat(existing.getEmail()).isEqualTo("john+updated@x.com");
        assertThat(existing.getEnabled()).isFalse();
        assertThat(existing.getIsAdmin()).isTrue();
        assertThat(existing.getRoles()).containsExactly(userRole);
    }

    @Test
    void hasNoAccessAssigned_reflectsWhetherAccessWasConfigured() {
        User user = new User("john", "john@x.com", "secret", false);
        Modules users = module(1L, "Users");
        Roles userRole = role(2L, "USER");
        Submodules readUsers = submodule(3L, "ReadUsers", users);

        assertThat(user.hasNoAccessAssigned()).isTrue();

        user.replaceAccess(UserAccess.of(Set.of(userRole), Set.of(users), Set.of(readUsers)));

        assertThat(user.hasNoAccessAssigned()).isFalse();
    }

    private static Roles role(Long id, String name) {
        Roles role = new Roles(name);
        role.setId(id);
        return role;
    }

    private static Modules module(Long id, String name) {
        Modules module = new Modules(name);
        module.setId(id);
        return module;
    }

    private static Submodules submodule(Long id, String name, Modules module) {
        Submodules submodule = new Submodules(name, module);
        submodule.setId(id);
        return submodule;
    }
}
