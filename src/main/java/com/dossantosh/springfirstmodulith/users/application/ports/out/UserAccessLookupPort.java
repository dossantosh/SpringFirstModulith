package com.dossantosh.springfirstmodulith.users.application.ports.out;

import com.dossantosh.springfirstmodulith.users.domain.Roles;

import java.util.List;

public interface UserAccessLookupPort {

	List<Roles> findRolesById(List<Long> ids);
}
