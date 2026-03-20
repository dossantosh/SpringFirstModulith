package com.dossantosh.springfirstmodulith.users.infrastructure.repos;

import com.dossantosh.springfirstmodulith.users.domain.Modules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Modules, Long> {

	Optional<Modules> findByNameIgnoreCase(String name);
}
