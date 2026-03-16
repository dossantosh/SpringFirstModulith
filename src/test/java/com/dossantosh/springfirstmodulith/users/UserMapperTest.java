package com.dossantosh.springfirstmodulith.users;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.dossantosh.springfirstmodulith.core.errors.custom.BusinessException;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.ModuleJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.RoleJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.UserJpaEntity;
import com.dossantosh.springfirstmodulith.users.infrastructure.mappers.UserMapper;

class UserMapperTest {

    @Test
    void toDomain_whenPersistedAccessIsIncomplete_failsFast() {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(1L);
        entity.setUsername("john");
        entity.setEmail("john@x.com");
        entity.setEnabled(true);
        entity.setPassword("hashed");
        entity.setIsAdmin(false);

        RoleJpaEntity role = new RoleJpaEntity();
        role.setId(2L);
        role.setName("USER");
        entity.setRoles(Set.of(role));

        ModuleJpaEntity module = new ModuleJpaEntity();
        module.setId(3L);
        module.setName("Users");
        entity.setModules(Set.of(module));

        assertThatThrownBy(() -> UserMapper.toDomain(entity))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Persisted user access is incomplete");
    }
}
