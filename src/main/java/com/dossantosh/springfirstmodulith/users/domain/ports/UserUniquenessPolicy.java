package com.dossantosh.springfirstmodulith.users.domain.ports;

public interface UserUniquenessPolicy {

	UserUniquenessPolicy PERMISSIVE = new UserUniquenessPolicy() {
		@Override
		public boolean usernameExists(String username) {
			return false;
		}

		@Override
		public boolean emailExists(String email) {
			return false;
		}
	};

	boolean usernameExists(String username);

	boolean emailExists(String email);
}
