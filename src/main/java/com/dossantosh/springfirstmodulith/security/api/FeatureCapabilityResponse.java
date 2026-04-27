package com.dossantosh.springfirstmodulith.security.api;

public record FeatureCapabilityResponse(boolean canAccess, boolean canRead, boolean canCreate, boolean canUpdate,
		boolean canDelete) {
}
