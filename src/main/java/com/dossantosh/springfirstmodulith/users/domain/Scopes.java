package com.dossantosh.springfirstmodulith.users.domain;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "scopes")
public class Scopes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_scope")
	private Long id;

	@Column(unique = true, length = 80, nullable = false)
	private String name;

	@Column(length = 255)
	private String description;

	private Scopes(Long id, String name, String description) {
		this.id = id;
		this.name = normalizeRequiredName(name, "scope name");
		this.description = normalizeNullable(description);
		if (id != null && id <= 0) {
			throw new BusinessException("scope id must be positive");
		}
	}

	protected Scopes() {
	}

	public static Scopes named(String name, String description) {
		return new Scopes(null, name, description);
	}

	public static Scopes reference(Long id, String name, String description) {
		return new Scopes(id, name, description);
	}

	public Long id() {
		return id;
	}

	public String name() {
		return name;
	}

	public String description() {
		return description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Scopes scopes)) {
			return false;
		}
		if (id != null && scopes.id != null) {
			return Objects.equals(id, scopes.id);
		}
		return Objects.equals(name, scopes.name);
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

	private static String normalizeNullable(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
