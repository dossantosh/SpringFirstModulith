package com.dossantosh.springfirstmodulith.users.api.requests;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UserAccessRequest(@NotEmpty(message = "roleIds is required") List<Long> roleIds) {
}
