package com.dossantosh.springfirstmodulith.users.application.views;

import com.dossantosh.springfirstmodulith.users.domain.ContractType;
import com.dossantosh.springfirstmodulith.users.domain.EmployeeStatus;

import java.io.Serializable;
import java.time.LocalDate;

public record UserPersonalDataView(Long userId, String username, String employeeCode, String firstName, String lastName,
		String corporateEmail, String phone, String identityDocument, LocalDate birthDate, String address, String city,
		String stateProvince, String postalCode, String country, String jobTitle, String department, LocalDate hireDate,
		EmployeeStatus status, ContractType contractType, String internalNotes) implements Serializable {
}
