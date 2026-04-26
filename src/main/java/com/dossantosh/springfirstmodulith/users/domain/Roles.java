package com.dossantosh.springfirstmodulith.users.domain;

import com.dossantosh.springfirstmodulith.core.exceptions.custom.BusinessException;
import jakarta.persistence.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Roles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_role")
	private Long id;

	@Column(unique = true, length = 20)
	private String name;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "role_scopes", joinColumns = @JoinColumn(name = "id_role", referencedColumnName = "id_role"), inverseJoinColumns = @JoinColumn(name = "id_scope", referencedColumnName = "id_scope"))
	private final Set<Scopes> scopes = new LinkedHashSet<>();

	private Roles(Long id, String name) {
		this.id = id;
		this.name = normalizeRequiredName(name, "role name");
		if (id != null && id <= 0) {
			throw new BusinessException("role id must be positive");
		}
	}

	protected Roles() {
	}

	public static Roles named(String name) {
		return new Roles(null, name);
	}

	public static Roles reference(Long id, String name) {
		return new Roles(id, name);
	}

	public Long id() {
		return id;
	}

	public String name() {
		return name;
	}

	public Set<Scopes> scopes() {
		return Collections.unmodifiableSet(scopes);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Roles roles)) {
			return false;
		}
		if (id != null && roles.id != null) {
			return Objects.equals(id, roles.id);
		}
		return Objects.equals(name, roles.name);
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
