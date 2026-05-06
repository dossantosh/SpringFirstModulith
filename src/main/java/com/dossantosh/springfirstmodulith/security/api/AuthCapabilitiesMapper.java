package com.dossantosh.springfirstmodulith.security.api;

import com.dossantosh.springfirstmodulith.authorization.AuthorizationScopes;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class AuthCapabilitiesMapper {

	private static final CapabilityDefinition SYSTEMS = new CapabilityDefinition(AuthorizationScopes.SYSTEMS_READ,
			AuthorizationScopes.SYSTEMS_WRITE);

	private static final CapabilityDefinition PERFUMES = new CapabilityDefinition(AuthorizationScopes.PERFUMES_READ,
			AuthorizationScopes.PERFUMES_WRITE);

	private AuthCapabilitiesMapper() {
	}

	public static AuthCapabilitiesResponse fromScopes(Collection<String> scopes) {
		Set<String> current = normalizeScopes(scopes);

		return new AuthCapabilitiesResponse(capabilityFor(current, SYSTEMS), capabilityFor(current, PERFUMES));
	}

	private static Set<String> normalizeScopes(Collection<String> scopes) {
		if (scopes == null || scopes.isEmpty()) {
			return Set.of();
		}
		return scopes.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
	}

	private static FeatureCapabilityResponse capabilityFor(Set<String> scopes, CapabilityDefinition definition) {
		boolean canRead = scopes.contains(definition.read());
		boolean canWrite = scopes.contains(definition.write());

		return new FeatureCapabilityResponse(canRead || canWrite, canRead, canWrite, canWrite, canWrite);
	}

	private record CapabilityDefinition(String read, String write) {
	}
}
