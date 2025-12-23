package com.dossantosh.springfirstmodulith.users.application.dtos.roles;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModulesDTO implements Serializable {

    private Long id;
    private String name;

}