package com.dossantosh.springfirstmodulith.users.infrastructure.repos;

import com.dossantosh.springfirstmodulith.users.domain.entities.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataEmployeeProfileJpaRepository extends JpaRepository<EmployeeProfile, Long> {

	Optional<EmployeeProfile> findByUserId(Long userId);
}
