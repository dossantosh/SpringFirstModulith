package com.dossantosh.springfirstmodulith.security.api;

public record FeatureCapabilityResponse(boolean access, boolean read, boolean write, boolean canRead, boolean canCreate,
		boolean canUpdate, boolean canDelete) {

	public FeatureCapabilityResponse(boolean read, boolean write, boolean canCreate, boolean canUpdate,
			boolean canDelete) {
		this(read || write, read, write, read, canCreate, canUpdate, canDelete);
	}
}
