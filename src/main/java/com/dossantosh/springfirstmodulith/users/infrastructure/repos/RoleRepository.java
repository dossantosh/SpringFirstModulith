package com.dossantosh.springfirstmodulith.users.infrastructure.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dossantosh.springfirstmodulith.users.infrastructure.entities.RoleJpaEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleJpaEntity, Long> {

    Optional<RoleJpaEntity> findByNameIgnoreCase(String name);
}
