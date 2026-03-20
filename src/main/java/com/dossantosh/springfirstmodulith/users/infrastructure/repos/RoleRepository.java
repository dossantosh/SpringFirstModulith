package com.dossantosh.springfirstmodulith.users.infrastructure.repos;

import com.dossantosh.springfirstmodulith.users.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {

	Optional<Roles> findByNameIgnoreCase(String name);
}
