package com.dossantosh.springfirstmodulith.users.domain;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "modules")
public class Modules {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_module")
	private Long id;

	@Column(unique = true, length = 50, nullable = false)
	private String name;

	private Modules(Long id, String name) {
		this.id = id;
		this.name = normalizeRequiredName(name, "module name");
		if (id != null && id <= 0) {
			throw new BusinessException("module id must be positive");
		}
	}

	protected Modules() {
	}

	public static Modules named(String name) {
		return new Modules(null, name);
	}

	public static Modules reference(Long id, String name) {
		return new Modules(id, name);
	}

	public Long id() {
		return id;
	}

	public String name() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Modules modules)) {
			return false;
		}
		if (id != null && modules.id != null) {
			return Objects.equals(id, modules.id);
		}
		return Objects.equals(name, modules.name);
	}

	@Override
	public int hashCode() {
		return id != null ? Objects.hash(id) : Objects.hash(name);
	}

	private static String normalizeRequiredName(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new BusinessException(fieldName + " cannot be blank");
		}
		return value.trim();
	}
}
