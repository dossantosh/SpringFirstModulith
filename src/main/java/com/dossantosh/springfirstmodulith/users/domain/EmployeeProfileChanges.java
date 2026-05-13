package com.dossantosh.springfirstmodulith.users.domain;

import java.time.LocalDate;

public record EmployeeProfileChanges(String employeeCode, String firstName, String lastName, String corporateEmail,
		String phone, String identityDocument, LocalDate birthDate, String address, String city, String stateProvince,
		String postalCode, String country, String jobTitle, String department, LocalDate hireDate,
		EmployeeStatus status, ContractType contractType, String internalNotes) {
}
