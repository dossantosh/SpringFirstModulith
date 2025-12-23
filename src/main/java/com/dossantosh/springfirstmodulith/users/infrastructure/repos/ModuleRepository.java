package com.dossantosh.springfirstmodulith.users.infrastructure.repos;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dossantosh.springfirstmodulith.users.domain.Modules;

@Repository
public interface ModuleRepository extends JpaRepository<Modules, Long> {
    
}
