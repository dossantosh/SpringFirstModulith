package com.dossantosh.springfirstmodulith.users.infrastructure.repos;

import com.dossantosh.springfirstmodulith.users.domain.Submodules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmoduleRepository extends JpaRepository<Submodules, Long> {

	@Query("""
			select s
			from Submodules s
			join s.module m
			where lower(m.name) = lower(:moduleName)
			  and lower(s.name) = lower(:submoduleName)
			""")
	Optional<Submodules> findByModuleNameAndSubmoduleName(@Param("moduleName") String moduleName,
			@Param("submoduleName") String submoduleName);
}
