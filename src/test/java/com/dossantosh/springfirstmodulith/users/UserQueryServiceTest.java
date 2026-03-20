package com.dossantosh.springfirstmodulith.users;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.application.ports.out.UserQueryPort;
import com.dossantosh.springfirstmodulith.users.application.services.UserQueryService;
import com.dossantosh.springfirstmodulith.users.application.views.UserSummaryView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

	@Mock
	private UserQueryPort userQueryPort;

	@InjectMocks
	private UserQueryService userQueryService;

	@Test
	void findUsersKeyset_next_firstPage_hasMore_setsCursorsAndFlags() {
		int limit = 2;

		UserQueryPort.UserSummaryRow p1 = new UserQueryPort.UserSummaryRow(10L, "u1", "e1", true, false);
		UserQueryPort.UserSummaryRow p2 = new UserQueryPort.UserSummaryRow(11L, "u2", "e2", true, false);
		UserQueryPort.UserSummaryRow extra = new UserQueryPort.UserSummaryRow(12L, "u3", "e3", true, false);

		when(userQueryPort.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
				.thenReturn(new ArrayList<>(List.of(p1, p2, extra)));

		KeysetPage<UserSummaryView> page = userQueryService.findUsersKeyset(null, null, null, null, limit,
				Direction.NEXT);

		assertThat(page.getContent()).extracting(UserSummaryView::id).containsExactly(10L, 11L);
		assertThat(page.getNextId()).isEqualTo(11L);
		assertThat(page.getPreviousId()).isEqualTo(10L);
		assertThat(page.isHasNext()).isTrue();
		assertThat(page.isHasPrevious()).isFalse();
	}

	@Test
	void findUsersKeyset_next_firstPage_noMore_setsHasNextFalse() {
		int limit = 2;

		UserQueryPort.UserSummaryRow p1 = new UserQueryPort.UserSummaryRow(10L, "u1", "e1", true, false);
		UserQueryPort.UserSummaryRow p2 = new UserQueryPort.UserSummaryRow(11L, "u2", "e2", true, false);

		when(userQueryPort.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
				.thenReturn(new ArrayList<>(List.of(p1, p2)));

		KeysetPage<UserSummaryView> page = userQueryService.findUsersKeyset(null, null, null, null, limit,
				Direction.NEXT);

		assertThat(page.getContent()).extracting(UserSummaryView::id).containsExactly(10L, 11L);
		assertThat(page.isHasNext()).isFalse();
		assertThat(page.isHasPrevious()).isFalse();
	}

	@Test
	void findUsersKeyset_previous_hasMore_reversesToAscending_andSetsFlags() {
		int limit = 2;
		Long lastId = 20L;

		UserQueryPort.UserSummaryRow p1 = new UserQueryPort.UserSummaryRow(11L, "u2", "u2@x.com", true, false);
		UserQueryPort.UserSummaryRow p2 = new UserQueryPort.UserSummaryRow(10L, "u1", "u1@x.com", true, false);
		UserQueryPort.UserSummaryRow extra = new UserQueryPort.UserSummaryRow(9L, "u0", "u0@x.com", true, false);

		when(userQueryPort.findUsersKeyset(null, null, null, lastId, limit + 1, Direction.PREVIOUS.name()))
				.thenReturn(new ArrayList<>(List.of(p1, p2, extra)));

		KeysetPage<UserSummaryView> page = userQueryService.findUsersKeyset(null, null, null, lastId, limit,
				Direction.PREVIOUS);

		assertThat(page.getContent()).extracting(UserSummaryView::id).containsExactly(10L, 11L);
		assertThat(page.getPreviousId()).isEqualTo(10L);
		assertThat(page.getNextId()).isEqualTo(11L);
		assertThat(page.isHasPrevious()).isTrue();
		assertThat(page.isHasNext()).isTrue();
	}

	@Test
	void findUsersKeyset_whenNoResults_returnsEmptyPageWithNullCursors() {
		int limit = 10;

		when(userQueryPort.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
				.thenReturn(new ArrayList<>());

		KeysetPage<UserSummaryView> page = userQueryService.findUsersKeyset(null, null, null, null, limit,
				Direction.NEXT);

		assertThat(page.getContent()).isEmpty();
		assertThat(page.getNextId()).isNull();
		assertThat(page.getPreviousId()).isNull();
		assertThat(page.isHasNext()).isFalse();
		assertThat(page.isHasPrevious()).isFalse();
	}
}
