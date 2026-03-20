package com.dossantosh.springfirstmodulith.users.infrastructure.projections;

public interface UserProjection {
	Long getId();

	String getUsername();

	String getEmail();

	Boolean getEnabled();

	Boolean getIsAdmin();
}
