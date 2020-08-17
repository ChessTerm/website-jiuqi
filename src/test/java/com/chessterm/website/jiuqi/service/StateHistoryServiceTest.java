package com.chessterm.website.jiuqi.service;

import com.chessterm.website.jiuqi.model.Board;
import com.chessterm.website.jiuqi.model.Game;
import com.chessterm.website.jiuqi.model.StateHistory;
import com.chessterm.website.jiuqi.model.User;
import com.chessterm.website.jiuqi.repository.StateRepository;
import com.chessterm.website.jiuqi.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StateHistoryServiceTest.TestContextConfiguration.class})
public class StateHistoryServiceTest {

    private final AtomicBoolean deleted = new AtomicBoolean(false);

    private final AtomicBoolean saved = new AtomicBoolean(false);

    @Autowired
    StateHistoryService service;

    @MockBean
    StateRepository repository;

    private Board board;

    private StateHistory lastState;

    @BeforeEach
    private void setup() {
        User user = new User(10000);
        Game game = TestData.testGame();
        board = new Board(game, user);
        lastState = new StateHistory();
        lastState.setBoard(board);
        lastState.setState(TestData.randomState());
        when(repository.findFirstByBoardIdOrderByTimestampDesc(board.getId())).thenReturn(lastState);
        deleted.set(false);
        doAnswer(invocation -> {
            deleted.set(true);
            return null;
        }).when(repository).deleteByBoardId(board.getId());
        saved.set(false);
        doAnswer(invocation -> {
            saved.set(true);
            return null;
        }).when(repository).save(any(StateHistory.class));
    }

    @Test
    public void saveBoardState_duplicate() {
        service.save(board, lastState.getState());
        assertThat(deleted.get()).isFalse();
        assertThat(saved.get()).isFalse();
    }

    @Test
    public void saveBoardState_initial() {
        service.save(board, board.getGame().getInitialState());
        assertThat(deleted.get()).isTrue();
        assertThat(saved.get()).isTrue();
    }

    @Test
    public void saveBoardState_regular() {
        service.save(board, TestData.randomState());
        assertThat(deleted.get()).isFalse();
        assertThat(saved.get()).isTrue();
    }

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        public StateHistoryService historyService() {
            return new StateHistoryService();
        }
    }
}
