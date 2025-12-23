package com.dossantosh.springfirstmodulith.users.application.dtos;

import java.io.Serializable;
import java.util.LinkedHashSet;

import com.dossantosh.springfirstmodulith.users.application.dtos.roles.ModulesDTO;
import com.dossantosh.springfirstmodulith.users.application.dtos.roles.RolesDTO;
import com.dossantosh.springfirstmodulith.users.application.dtos.roles.SubmodulesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FullUserDTO implements Serializable {

    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
    private Boolean isAdmin;

    private LinkedHashSet<RolesDTO> roles = new LinkedHashSet<>();
    private LinkedHashSet<ModulesDTO> modules = new LinkedHashSet<>();
    private LinkedHashSet<SubmodulesDTO> submodules = new LinkedHashSet<>();

    public FullUserDTO(Long id, String username, String email, Boolean enabled, Boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.isAdmin = isAdmin;
    }
}