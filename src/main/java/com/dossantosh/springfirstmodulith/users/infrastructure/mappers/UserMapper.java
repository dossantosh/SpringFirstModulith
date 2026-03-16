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

        UserAccess access = toUserAccessOrNull(entity);

        return User.rehydrate(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getEnabled(),
                entity.getPassword(),
                entity.getIsAdmin(),
                access);
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

    private static UserAccess toUserAccessOrNull(UserJpaEntity entity) {
        boolean hasAnyAccess = entity.getRoles() != null && !entity.getRoles().isEmpty()
                || entity.getModules() != null && !entity.getModules().isEmpty()
                || entity.getSubmodules() != null && !entity.getSubmodules().isEmpty();

        if (!hasAnyAccess) {
            return null;
        }

        boolean hasCompleteAccess = entity.getRoles() != null && !entity.getRoles().isEmpty()
                && entity.getModules() != null && !entity.getModules().isEmpty()
                && entity.getSubmodules() != null && !entity.getSubmodules().isEmpty();

        if (!hasCompleteAccess) {
            throw new com.dossantosh.springfirstmodulith.core.errors.custom.BusinessException(
                    "Persisted user access is incomplete");
        }

        return UserAccess.of(
                AccessReferenceMapper.toDomainRoles(entity.getRoles()),
                AccessReferenceMapper.toDomainModules(entity.getModules()),
                AccessReferenceMapper.toDomainSubmodules(entity.getSubmodules()));
    }
}
