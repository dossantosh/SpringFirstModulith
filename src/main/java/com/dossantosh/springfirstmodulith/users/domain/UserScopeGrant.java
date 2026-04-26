package com.dossantosh.springfirstmodulith.users.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_scope_grants")
public class UserScopeGrant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user_scope_grant")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_user")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_scope")
	private Scopes scope;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "granted_by")
	private User grantedBy;

	@Column(length = 255)
	private String reason;

	@Column(name = "expires_at")
	private OffsetDateTime expiresAt;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	protected UserScopeGrant() {
	}

	public Long id() {
		return id;
	}

	public User user() {
		return user;
	}

	public Scopes scope() {
		return scope;
	}

	public User grantedBy() {
		return grantedBy;
	}

	public String reason() {
		return reason;
	}

	public OffsetDateTime expiresAt() {
		return expiresAt;
	}

	public OffsetDateTime createdAt() {
		return createdAt;
	}
}
