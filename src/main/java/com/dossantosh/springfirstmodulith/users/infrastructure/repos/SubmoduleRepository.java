package com.dossantosh.springfirstmodulith.users.infrastructure.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dossantosh.springfirstmodulith.users.domain.Submodules;

@Repository
public interface SubmoduleRepository extends JpaRepository<Submodules, Long> {

}