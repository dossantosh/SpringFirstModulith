package com.dossantosh.springfirstmodulith.users.application.views;

import java.io.Serializable;

public record UserSummaryView(Long id, String username, String email, Boolean enabled,
		Boolean isAdmin) implements Serializable {
}
