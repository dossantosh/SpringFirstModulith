package com.dossantosh.springfirstmodulith.users.domain;

import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Submodules {

    private Long id;

    private String name;

    private Modules module;

    public Submodules(String name, Modules module) {
        this.name = name;
        this.module = module;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModule(Modules module) {
        this.module = module;
    }

    public boolean belongsTo(Modules module) {
        return this.module != null && this.module.equals(module);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Submodules that = (Submodules) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
