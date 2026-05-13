package com.dossantosh.springfirstmodulith.users.infrastructure.repos;

import com.dossantosh.springfirstmodulith.users.domain.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataRoleJpaRepository extends JpaRepository<Roles, Long> {

	Optional<Roles> findByNameIgnoreCase(String name);

	List<Roles> findAllByOrderByNameAsc();
}
