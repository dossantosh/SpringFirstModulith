package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.users.application.ports.out.EmployeeProfileRepository;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserRepository;
import com.dossantosh.springfirstmodulith.users.application.views.UserPersonalDataView;
import com.dossantosh.springfirstmodulith.users.domain.*;
import com.dossantosh.springfirstmodulith.users.domain.entities.EmployeeProfile;
import com.dossantosh.springfirstmodulith.users.domain.entities.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPersonalDataServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private EmployeeProfileRepository employeeProfileRepository;

	@InjectMocks
	private UserPersonalDataService userPersonalDataService;

	@Test
	void getPersonalData_whenProfileMissing_returnsDefaultViewFromUser() {
		User user = user();

		when(userRepository.findById(7L)).thenReturn(Optional.of(user));
		when(employeeProfileRepository.findByUserId(7L)).thenReturn(Optional.empty());

		UserPersonalDataView view = userPersonalDataService.getPersonalData(7L);

		assertThat(view.userId()).isEqualTo(7L);
		assertThat(view.username()).isEqualTo("ana");
		assertThat(view.corporateEmail()).isEqualTo("ana@example.com");
		assertThat(view.status()).isEqualTo(EmployeeStatus.ACTIVE);
	}

	@Test
	void updatePersonalData_whenProfileMissing_createsProfileForUser() {
		User user = user();

		when(userRepository.findById(7L)).thenReturn(Optional.of(user));
		when(employeeProfileRepository.findByUserId(7L)).thenReturn(Optional.empty());
		when(employeeProfileRepository.save(any(EmployeeProfile.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		EmployeeProfileChanges changes = new EmployeeProfileChanges(" EMP-7 ", " Ana ", " Lopez ",
				"ana.lopez@company.local", "+34", "DNI7", LocalDate.of(1990, 1, 1), "Street", "Madrid", "Madrid",
				"28001", "Espana", "Analista", "Sistemas", LocalDate.of(2024, 1, 1), EmployeeStatus.ACTIVE,
				ContractType.FULL_TIME, "Notas");

		UserPersonalDataView view = userPersonalDataService.updatePersonalData(7L, changes);

		ArgumentCaptor<EmployeeProfile> captor = ArgumentCaptor.forClass(EmployeeProfile.class);
		verify(employeeProfileRepository).save(captor.capture());
		assertThat(captor.getValue().employeeCode()).isEqualTo("EMP-7");
		assertThat(view.firstName()).isEqualTo("Ana");
		assertThat(view.contractType()).isEqualTo(ContractType.FULL_TIME);
	}

	@Test
	void getPersonalData_whenUserMissing_throwsEntityNotFound() {
		when(userRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userPersonalDataService.getPersonalData(99L))
				.isInstanceOf(EntityNotFoundException.class).hasMessageContaining("99");

		verifyNoInteractions(employeeProfileRepository);
	}

	private static User user() {
		return User.rehydrate(7L, "ana", "ana@example.com", true, "hash", false, null);
	}
}
