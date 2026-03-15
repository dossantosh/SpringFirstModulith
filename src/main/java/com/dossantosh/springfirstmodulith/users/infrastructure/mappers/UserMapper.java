package com.dossantosh.springfirstmodulith.users.infrastructure.mappers;

import com.dossantosh.springfirstmodulith.users.domain.User;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import com.dossantosh.springfirstmodulith.users.infrastructure.entities.UserJpaEntity;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setEnabled(entity.getEnabled());
        user.setPassword(entity.getPassword());
        user.setIsAdmin(entity.getIsAdmin());

        if (entity.getRoles() != null && entity.getModules() != null && entity.getSubmodules() != null
                && !entity.getRoles().isEmpty() && !entity.getModules().isEmpty() && !entity.getSubmodules().isEmpty()) {
            user.replaceAccess(UserAccess.of(
                    AccessReferenceMapper.toDomainRoles(entity.getRoles()),
                    AccessReferenceMapper.toDomainModules(entity.getModules()),
                    AccessReferenceMapper.toDomainSubmodules(entity.getSubmodules())));
        }

        return user;
    }

    public static UserJpaEntity toJpaEntity(User user) {
        if (user == null) {
            return null;
        }

        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setEnabled(user.getEnabled());
        entity.setPassword(user.getPassword());
        entity.setIsAdmin(user.getIsAdmin());
        entity.setRoles(AccessReferenceMapper.toJpaRoleEntities(user.getRoles()));
        entity.setModules(AccessReferenceMapper.toJpaModuleEntities(user.getModules()));
        entity.setSubmodules(AccessReferenceMapper.toJpaSubmoduleEntities(user.getSubmodules()));

        return entity;
    }
}
