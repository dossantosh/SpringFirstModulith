package com.dossantosh.springfirstmodulith.users.domain.entities;

import com.dossantosh.springfirstmodulith.users.domain.ContractType;
import com.dossantosh.springfirstmodulith.users.domain.EmployeeProfileChanges;
import com.dossantosh.springfirstmodulith.users.domain.EmployeeStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "employee_profiles")
public class EmployeeProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_employee_profile")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_user", nullable = false, unique = true)
	private User user;

	@Column(name = "employee_code", length = 30, unique = true)
	private String employeeCode;

	@Column(name = "first_name", length = 80)
	private String firstName;

	@Column(name = "last_name", length = 120)
	private String lastName;

	@Column(name = "corporate_email", length = 120)
	private String corporateEmail;

	@Column(length = 30)
	private String phone;

	@Column(name = "identity_document", length = 40)
	private String identityDocument;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Column(length = 160)
	private String address;

	@Column(length = 80)
	private String city;

	@Column(name = "state_province", length = 80)
	private String stateProvince;

	@Column(name = "postal_code", length = 20)
	private String postalCode;

	@Column(length = 80)
	private String country;

	@Column(name = "job_title", length = 100)
	private String jobTitle;

	@Column(length = 100)
	private String department;

	@Column(name = "hire_date")
	private LocalDate hireDate;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private EmployeeStatus status = EmployeeStatus.ACTIVE;

	@Enumerated(EnumType.STRING)
	@Column(name = "contract_type", length = 30)
	private ContractType contractType;

	@Column(name = "internal_notes", length = 1000)
	private String internalNotes;

	protected EmployeeProfile() {
	}

	public EmployeeProfile(User user) {
		this.user = user;
	}

	public Long id() {
		return id;
	}

	public User user() {
		return user;
	}

	public String employeeCode() {
		return employeeCode;
	}

	public String firstName() {
		return firstName;
	}

	public String lastName() {
		return lastName;
	}

	public String corporateEmail() {
		return corporateEmail;
	}

	public String phone() {
		return phone;
	}

	public String identityDocument() {
		return identityDocument;
	}

	public LocalDate birthDate() {
		return birthDate;
	}

	public String address() {
		return address;
	}

	public String city() {
		return city;
	}

	public String stateProvince() {
		return stateProvince;
	}

	public String postalCode() {
		return postalCode;
	}

	public String country() {
		return country;
	}

	public String jobTitle() {
		return jobTitle;
	}

	public String department() {
		return department;
	}

	public LocalDate hireDate() {
		return hireDate;
	}

	public EmployeeStatus status() {
		return status;
	}

	public ContractType contractType() {
		return contractType;
	}

	public String internalNotes() {
		return internalNotes;
	}

	public void applyChangesFrom(EmployeeProfileChanges changes) {
		this.employeeCode = normalizeNullable(changes.employeeCode());
		this.firstName = normalizeNullable(changes.firstName());
		this.lastName = normalizeNullable(changes.lastName());
		this.corporateEmail = normalizeNullable(changes.corporateEmail());
		this.phone = normalizeNullable(changes.phone());
		this.identityDocument = normalizeNullable(changes.identityDocument());
		this.birthDate = changes.birthDate();
		this.address = normalizeNullable(changes.address());
		this.city = normalizeNullable(changes.city());
		this.stateProvince = normalizeNullable(changes.stateProvince());
		this.postalCode = normalizeNullable(changes.postalCode());
		this.country = normalizeNullable(changes.country());
		this.jobTitle = normalizeNullable(changes.jobTitle());
		this.department = normalizeNullable(changes.department());
		this.hireDate = changes.hireDate();
		this.status = changes.status() == null ? EmployeeStatus.ACTIVE : changes.status();
		this.contractType = changes.contractType();
		this.internalNotes = normalizeNullable(changes.internalNotes());
	}

	private static String normalizeNullable(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
