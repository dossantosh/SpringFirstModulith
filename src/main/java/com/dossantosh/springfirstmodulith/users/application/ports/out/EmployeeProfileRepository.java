package com.dossantosh.springfirstmodulith.users.application.ports.out;

import com.dossantosh.springfirstmodulith.users.domain.entities.EmployeeProfile;

import java.util.Optional;

public interface EmployeeProfileRepository {

	Optional<EmployeeProfile> findByUserId(Long userId);

	EmployeeProfile save(EmployeeProfile profile);
}
