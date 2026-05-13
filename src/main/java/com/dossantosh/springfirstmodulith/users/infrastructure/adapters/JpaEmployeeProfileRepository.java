package com.dossantosh.springfirstmodulith.users.infrastructure.adapters;

import com.dossantosh.springfirstmodulith.users.application.ports.out.EmployeeProfileRepository;
import com.dossantosh.springfirstmodulith.users.domain.entities.EmployeeProfile;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.SpringDataEmployeeProfileJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class JpaEmployeeProfileRepository implements EmployeeProfileRepository {

	private final SpringDataEmployeeProfileJpaRepository springDataEmployeeProfileJpaRepository;

	JpaEmployeeProfileRepository(SpringDataEmployeeProfileJpaRepository springDataEmployeeProfileJpaRepository) {
		this.springDataEmployeeProfileJpaRepository = springDataEmployeeProfileJpaRepository;
	}

	@Override
	public Optional<EmployeeProfile> findByUserId(Long userId) {
		return springDataEmployeeProfileJpaRepository.findByUserId(userId);
	}

	@Override
	public EmployeeProfile save(EmployeeProfile profile) {
		return springDataEmployeeProfileJpaRepository.save(profile);
	}
}
