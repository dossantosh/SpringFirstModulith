package com.dossantosh.springfirstmodulith.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dossantosh.springfirstmodulith.core.page.Direction;
import com.dossantosh.springfirstmodulith.core.page.KeysetPage;
import com.dossantosh.springfirstmodulith.users.application.services.UserQueryService;
import com.dossantosh.springfirstmodulith.users.application.views.UserSummaryView;
import com.dossantosh.springfirstmodulith.users.infrastructure.projections.UserProjection;
import com.dossantosh.springfirstmodulith.users.infrastructure.repos.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryService userQueryService;

    @Test
    void findUsersKeyset_next_firstPage_hasMore_setsCursorsAndFlags() {
        int limit = 2;

        UserProjection p1 = mock(UserProjection.class);
        when(p1.getId()).thenReturn(10L);
        when(p1.getUsername()).thenReturn("u1");
        when(p1.getEmail()).thenReturn("e1");
        when(p1.getEnabled()).thenReturn(true);
        when(p1.getIsAdmin()).thenReturn(false);

        UserProjection p2 = mock(UserProjection.class);
        when(p2.getId()).thenReturn(11L);
        when(p2.getUsername()).thenReturn("u2");
        when(p2.getEmail()).thenReturn("e2");
        when(p2.getEnabled()).thenReturn(true);
        when(p2.getIsAdmin()).thenReturn(false);

        UserProjection extra = mock(UserProjection.class);

        when(userRepository.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
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

        UserProjection p1 = mock(UserProjection.class);
        when(p1.getId()).thenReturn(10L);
        when(p1.getUsername()).thenReturn("u1");
        when(p1.getEmail()).thenReturn("e1");
        when(p1.getEnabled()).thenReturn(true);
        when(p1.getIsAdmin()).thenReturn(false);

        UserProjection p2 = mock(UserProjection.class);
        when(p2.getId()).thenReturn(11L);
        when(p2.getUsername()).thenReturn("u2");
        when(p2.getEmail()).thenReturn("e2");
        when(p2.getEnabled()).thenReturn(true);
        when(p2.getIsAdmin()).thenReturn(false);

        when(userRepository.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
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

        UserProjection p1 = mock(UserProjection.class);
        when(p1.getId()).thenReturn(11L);
        when(p1.getUsername()).thenReturn("u2");
        when(p1.getEmail()).thenReturn("u2@x.com");
        when(p1.getEnabled()).thenReturn(true);
        when(p1.getIsAdmin()).thenReturn(false);

        UserProjection p2 = mock(UserProjection.class);
        when(p2.getId()).thenReturn(10L);
        when(p2.getUsername()).thenReturn("u1");
        when(p2.getEmail()).thenReturn("u1@x.com");
        when(p2.getEnabled()).thenReturn(true);
        when(p2.getIsAdmin()).thenReturn(false);

        UserProjection extra = mock(UserProjection.class);

        when(userRepository.findUsersKeyset(null, null, null, lastId, limit + 1, Direction.PREVIOUS.name()))
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

        when(userRepository.findUsersKeyset(null, null, null, null, limit + 1, Direction.NEXT.name()))
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
