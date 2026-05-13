package com.dossantosh.springfirstmodulith.users.api.requests;

import com.dossantosh.springfirstmodulith.users.domain.ContractType;
import com.dossantosh.springfirstmodulith.users.domain.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserPersonalDataRequest(
		@Size(max = 30, message = "employeeCode length must be <= 30") String employeeCode,
		@Size(max = 80, message = "firstName length must be <= 80") String firstName,
		@Size(max = 120, message = "lastName length must be <= 120") String lastName,
		@Email(message = "corporateEmail format is invalid") @Size(max = 120, message = "corporateEmail length must be <= 120") String corporateEmail,
		@Size(max = 30, message = "phone length must be <= 30") String phone,
		@Size(max = 40, message = "identityDocument length must be <= 40") String identityDocument,
		@Past(message = "birthDate must be in the past") LocalDate birthDate,
		@Size(max = 160, message = "address length must be <= 160") String address,
		@Size(max = 80, message = "city length must be <= 80") String city,
		@Size(max = 80, message = "stateProvince length must be <= 80") String stateProvince,
		@Size(max = 20, message = "postalCode length must be <= 20") String postalCode,
		@Size(max = 80, message = "country length must be <= 80") String country,
		@Size(max = 100, message = "jobTitle length must be <= 100") String jobTitle,
		@Size(max = 100, message = "department length must be <= 100") String department,
		@PastOrPresent(message = "hireDate must be today or in the past") LocalDate hireDate, EmployeeStatus status,
		ContractType contractType,
		@Size(max = 1000, message = "internalNotes length must be <= 1000") String internalNotes) {
}
