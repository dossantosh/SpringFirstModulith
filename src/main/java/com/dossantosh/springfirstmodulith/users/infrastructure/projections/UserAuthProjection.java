package com.dossantosh.springfirstmodulith.users.infrastructure.projections;

import java.util.List;

public interface UserAuthProjection {
	Long getId();

	String getUsername();

	String getEmail();

	String getPassword();

	Boolean getEnabled();

	Boolean getIsAdmin();

	List<String> getRoles();

	List<String> getModules();

	List<String> getSubmodules();
}
