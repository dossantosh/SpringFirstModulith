package com.dossantosh.springfirstmodulith.users.domain;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "submodules", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "id_module"})})
public class Submodules {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_submodule")
	private Long id;

	@Column(length = 50)
	private String name;

	@ManyToOne
	@JoinColumn(name = "id_module")
	private Modules module;

	private Submodules(Long id, String name, Modules module) {
		this.id = id;
		this.name = normalizeRequiredName(name, "submodule name");
		if (id != null && id <= 0) {
			throw new BusinessException("submodule id must be positive");
		}
		if (module == null) {
			throw new BusinessException("submodule module cannot be null");
		}
		this.module = module;
	}

	protected Submodules() {
	}

	public static Submodules named(String name, Modules module) {
		return new Submodules(null, name, module);
	}

	public static Submodules reference(Long id, String name, Modules module) {
		return new Submodules(id, name, module);
	}

	public Long id() {
		return id;
	}

	public String name() {
		return name;
	}

	public Modules module() {
		return module;
	}

	public boolean belongsTo(Modules module) {
		return this.module.equals(module);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Submodules that)) {
			return false;
		}
		if (id != null && that.id != null) {
			return Objects.equals(id, that.id);
		}
		return Objects.equals(name, that.name) && Objects.equals(module, that.module);
	}

	@Override
	public int hashCode() {
		return id != null ? Objects.hash(id) : Objects.hash(name, module);
	}

	private static String normalizeRequiredName(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new BusinessException(fieldName + " cannot be blank");
		}
		return value.trim();
	}
}
