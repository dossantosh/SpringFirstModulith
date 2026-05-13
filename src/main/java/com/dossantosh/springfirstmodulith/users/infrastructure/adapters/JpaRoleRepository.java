package com.dossantosh.springfirstmodulith.users.infrastructure.adapters;

import com.dossantosh.springfirstmodulith.users.application.ports.out.RoleRepository;
import com.dossantosh.springfirstmodulith.users.domain.entities.Roles;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.SpringDataRoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class JpaRoleRepository implements RoleRepository {

	private final SpringDataRoleJpaRepository springDataRoleJpaRepository;

	JpaRoleRepository(SpringDataRoleJpaRepository springDataRoleJpaRepository) {
		this.springDataRoleJpaRepository = springDataRoleJpaRepository;
	}

	@Override
	public List<Roles> findRolesById(List<Long> ids) {
		return springDataRoleJpaRepository.findAllById(ids);
	}

	@Override
	public List<Roles> findAllRoles() {
		return springDataRoleJpaRepository.findAllByOrderByNameAsc();
	}

	@Override
	public Optional<Roles> findRoleByName(String roleName) {
		return springDataRoleJpaRepository.findByNameIgnoreCase(roleName);
	}
}
