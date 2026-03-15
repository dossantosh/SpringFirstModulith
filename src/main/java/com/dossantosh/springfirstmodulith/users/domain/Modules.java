package com.dossantosh.springfirstmodulith.users.domain;

import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Modules {

    private Long id;

    private String name;

    public Modules(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Modules modules = (Modules) o;
        return Objects.equals(id, modules.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
