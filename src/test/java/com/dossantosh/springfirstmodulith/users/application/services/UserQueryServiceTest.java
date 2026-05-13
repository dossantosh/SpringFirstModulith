package com.dossantosh.springfirstmodulith.users.application.services;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.application.ports.out.RoleRepository;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserRepository;
import com.dossantosh.springfirstmodulith.users.application.views.UserRolesView;
import com.dossantosh.springfirstmodulith.users.application.views.UserSummaryView;
import com.dossantosh.springfirstmodulith.users.domain.entities.Roles;
import com.dossantosh.springfirstmodulith.users.domain.entities.User;
import com.dossantosh.springfirstmodulith.users.domain.UserAccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@InjectMocks
	private UserQueryService userQueryService;

	@Test
	void findUsersKeyset_next_firstPage_hasMore_setsCursorsAndFlags() {
		int limit = 2;

		UserRepository.UserSummaryRow p1 = new UserRepository.UserSummaryRow(10L, "u1", "e1", true, false);
		UserRepository.UserSummaryRow p2 = new UserRepository.UserSummaryRow(11L, "u2", "e2", true, false);
		UserRepository.UserSummaryRow extra = new UserRepository.UserSummaryRow(12L, "u3", "e3", true, false);

		when(userRepository.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
				.thenReturn(new ArrayList<>(List.of(p1, p2, extra)));

		KeysetPage<UserSummaryView> page = userQueryService.findUsersKeyset(null, null, null, null, limit,
				Direction.NEXT);

		assertThat(page.content()).extracting(UserSummaryView::id).containsExactly(10L, 11L);
		assertThat(page.nextId()).isEqualTo(11L);
		assertThat(page.previousId()).isEqualTo(10L);
		assertThat(page.hasNext()).isTrue();
		assertThat(page.hasPrevious()).isFalse();
	}

	@Test
	void findUsersKeyset_next_firstPage_noMore_setsHasNextFalse() {
		int limit = 2;

		UserRepository.UserSummaryRow p1 = new UserRepository.UserSummaryRow(10L, "u1", "e1", true, false);
		UserRepository.UserSummaryRow p2 = new UserRepository.UserSummaryRow(11L, "u2", "e2", true, false);

		when(userRepository.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
				.thenReturn(new ArrayList<>(List.of(p1, p2)));

		KeysetPage<UserSummaryView> page = userQueryService.findUsersKeyset(null, null, null, null, limit,
				Direction.NEXT);

		assertThat(page.content()).extracting(UserSummaryView::id).containsExactly(10L, 11L);
		assertThat(page.hasNext()).isFalse();
		assertThat(page.hasPrevious()).isFalse();
	}

	@Test
	void findUsersKeyset_previous_hasMore_reversesToAscending_andSetsFlags() {
		int limit = 2;
		Long lastId = 20L;

		UserRepository.UserSummaryRow p1 = new UserRepository.UserSummaryRow(11L, "u2", "u2@x.com", true, false);
		UserRepository.UserSummaryRow p2 = new UserRepository.UserSummaryRow(10L, "u1", "u1@x.com", true, false);
		UserRepository.UserSummaryRow extra = new UserRepository.UserSummaryRow(9L, "u0", "u0@x.com", true, false);

		when(userRepository.findUsersKeyset(null, null, null, lastId, limit + 1, Direction.PREVIOUS.name()))
				.thenReturn(new ArrayList<>(List.of(p1, p2, extra)));

		KeysetPage<UserSummaryView> page = userQueryService.findUsersKeyset(null, null, null, lastId, limit,
				Direction.PREVIOUS);

		assertThat(page.content()).extracting(UserSummaryView::id).containsExactly(10L, 11L);
		assertThat(page.previousId()).isEqualTo(10L);
		assertThat(page.nextId()).isEqualTo(11L);
		assertThat(page.hasPrevious()).isTrue();
		assertThat(page.hasNext()).isTrue();
	}

	@Test
	void findUsersKeyset_whenNoResults_returnsEmptyPageWithNullCursors() {
		int limit = 10;

		when(userRepository.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
				.thenReturn(new ArrayList<>());

		KeysetPage<UserSummaryView> page = userQueryService.findUsersKeyset(null, null, null, null, limit,
				Direction.NEXT);

		assertThat(page.content()).isEmpty();
		assertThat(page.nextId()).isNull();
		assertThat(page.previousId()).isNull();
		assertThat(page.hasNext()).isFalse();
		assertThat(page.hasPrevious()).isFalse();
	}

	@Test
	void getUserRoles_returnsAssignedRolesAndAvailableCatalog() {
		Roles systems = Roles.reference(1L, "SYSTEMS");
		Roles perfumes = Roles.reference(2L, "PERFUMES");
		User user = User.rehydrate(7L, "ana", "ana@example.com", true, "hash", false, UserAccess.of(Set.of(systems)));

		when(userRepository.findFullUserById(7L)).thenReturn(Optional.of(user));
		when(roleRepository.findAllRoles()).thenReturn(List.of(perfumes, systems));

		UserRolesView roles = userQueryService.getUserRoles(7L);

		assertThat(roles.roles()).extracting("name").containsExactly("SYSTEMS");
		assertThat(roles.availableRoles()).extracting("name").containsExactly("PERFUMES", "SYSTEMS");
	}
}
