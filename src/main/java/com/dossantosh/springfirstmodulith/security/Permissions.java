package com.dossantosh.springfirstmodulith.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("permissions")
public class Permissions {

	public boolean canAccessUsers(Authentication authentication) {
		return hasAuthority(authentication, SecurityAuthorityNames.MODULE_USERS);
	}

	public boolean canReadUsers(Authentication authentication) {
		return canAccessUsers(authentication) && hasAuthority(authentication, SecurityAuthorityNames.SUBMODULE_READ_USERS);
	}

	public boolean canWriteUsers(Authentication authentication) {
		return canAccessUsers(authentication)
				&& hasAuthority(authentication, SecurityAuthorityNames.SUBMODULE_WRITE_USERS);
	}

	private boolean hasAuthority(Authentication authentication, String authority) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return false;
		}

		return authentication.getAuthorities().stream().anyMatch(granted -> authority.equals(granted.getAuthority()));
	}
}
