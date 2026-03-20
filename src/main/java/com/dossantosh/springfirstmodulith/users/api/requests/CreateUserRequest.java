package com.dossantosh.springfirstmodulith.users.api.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
		@NotBlank(message = "username is required") @Size(max = 40, message = "username length must be <= 40") String username,
		@NotBlank(message = "email is required") @Email(message = "email format is invalid") @Size(max = 100, message = "email length must be <= 100") String email,
		@NotBlank(message = "password is required") @Size(min = 8, max = 100, message = "password length must be between 8 and 100") String password,
		@NotNull(message = "isAdmin is required") Boolean isAdmin, @Valid UserAccessRequest access) {
}
