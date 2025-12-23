package com.dossantosh.springfirstmodulith.users.infrastructure.projections;

/**
 * Projection interface to fetch essential user authentication data.
 */
public interface UserProjection {
    Long getId();

    String getUsername();

    String getEmail();

    Boolean getEnabled();

    Boolean getIsAdmin();
}