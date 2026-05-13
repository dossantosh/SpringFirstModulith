package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.users.application.ports.out.EmployeeProfileRepository;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserRepository;
import com.dossantosh.springfirstmodulith.users.application.views.UserPersonalDataView;
import com.dossantosh.springfirstmodulith.users.domain.entities.EmployeeProfile;
import com.dossantosh.springfirstmodulith.users.domain.EmployeeProfileChanges;
import com.dossantosh.springfirstmodulith.users.domain.EmployeeStatus;
import com.dossantosh.springfirstmodulith.users.domain.entities.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPersonalDataService {

	private final UserRepository userRepository;
	private final EmployeeProfileRepository employeeProfileRepository;

	public UserPersonalDataService(UserRepository userRepository, EmployeeProfileRepository employeeProfileRepository) {
		this.userRepository = userRepository;
		this.employeeProfileRepository = employeeProfileRepository;
	}

	@Transactional(readOnly = true)
	public UserPersonalDataView getPersonalData(Long userId) {
		User user = findUser(userId);

		return employeeProfileRepository.findByUserId(userId).map(this::toPersonalDataView)
				.orElseGet(() -> emptyPersonalDataView(user));
	}

	@Transactional
	public UserPersonalDataView updatePersonalData(Long userId, EmployeeProfileChanges changes) {
		User user = findUser(userId);
		EmployeeProfile profile = employeeProfileRepository.findByUserId(userId)
				.orElseGet(() -> new EmployeeProfile(user));

		profile.applyChangesFrom(changes);
		return toPersonalDataView(employeeProfileRepository.save(profile));
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
	}

	private UserPersonalDataView emptyPersonalDataView(User user) {
		return new UserPersonalDataView(user.id(), user.username(), null, null, null, user.email(), null, null, null,
				null, null, null, null, null, null, null, null, EmployeeStatus.ACTIVE, null, null);
	}

	private UserPersonalDataView toPersonalDataView(EmployeeProfile profile) {
		User user = profile.user();
		return new UserPersonalDataView(user.id(), user.username(), profile.employeeCode(), profile.firstName(),
				profile.lastName(), profile.corporateEmail(), profile.phone(), profile.identityDocument(),
				profile.birthDate(), profile.address(), profile.city(), profile.stateProvince(), profile.postalCode(),
				profile.country(), profile.jobTitle(), profile.department(), profile.hireDate(), profile.status(),
				profile.contractType(), profile.internalNotes());
	}
}
