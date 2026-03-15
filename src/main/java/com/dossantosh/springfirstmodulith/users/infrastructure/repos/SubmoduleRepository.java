package com.dossantosh.springfirstmodulith.users.infrastructure.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dossantosh.springfirstmodulith.users.infrastructure.entities.SubmoduleJpaEntity;

@Repository
public interface SubmoduleRepository extends JpaRepository<SubmoduleJpaEntity, Long> {

    @Query("""
            select s
            from SubmoduleJpaEntity s
            join s.module m
            where lower(m.name) = lower(:moduleName)
              and lower(s.name) = lower(:submoduleName)
            """)
    Optional<SubmoduleJpaEntity> findByModuleNameAndSubmoduleName(
            @Param("moduleName") String moduleName,
            @Param("submoduleName") String submoduleName);
}
