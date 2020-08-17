package com.chessterm.website.jiuqi.service;

import com.chessterm.website.jiuqi.model.Board;
import com.chessterm.website.jiuqi.model.Game;
import com.chessterm.website.jiuqi.model.State;
import com.chessterm.website.jiuqi.model.User;
import com.chessterm.website.jiuqi.repository.BoardRepository;
import com.chessterm.website.jiuqi.util.TestData;
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
@ContextConfiguration(classes = {BoardServiceTest.TestContextConfiguration.class})
public class BoardServiceTest {

    private final Game game = TestData.testGame();

    @Autowired
    BoardService service;

    @MockBean
    BoardRepository repository;

    @MockBean
    StateHistoryService historyService;

    @Test
    public void autoCreate() {
        User user = new User(10000);
        Board board = new Board(game, user);
        when(repository.save(board)).thenReturn(board);
        Board result = service.get(user, game, true);
        assertThat(result).isEqualTo(board);
    }

    @Test
    public void getRole_ownerNoPassword() {
        User owner = new User(10000);
        Board board = new Board(game, owner);
        User user = new User(10001);
        assertThat(service.getRole(board, user).isWrite()).isTrue();
    }

    @Test
    public void getRole_userIsOwner() {
        User owner = new User(10000);
        owner.setPassword("password");
        Board board = new Board(game, owner);
        assertThat(service.getRole(board, owner).isWrite()).isTrue();
    }

    @Test
    public void getRole_userIsAdmin() {
        User owner = new User(10000);
        owner.setPassword("password");
        Board board = new Board(game, owner);
        User user = new User(10001, true);
        assertThat(service.getRole(board, user).isWrite()).isTrue();
    }

    @Test
    public void getRole_denied() {
        User owner = new User(10000);
        owner.setPassword("password");
        Board board = new Board(game, owner);
        User user = new User(10001);
        assertThat(service.getRole(board, user).isWrite()).isFalse();
    }

    @Test
    public void setState() {
        User user = new User(10000);
        Board board = new Board(game, user);
        AtomicBoolean historySaved = new AtomicBoolean(false);
        when(repository.save(board)).thenReturn(board);
        doAnswer(invocation -> {
            historySaved.set(true);
            return null;
        }).when(historyService).save(any(Board.class), any(State.class));
        State state = TestData.randomState();
        Board result = service.setState(board, state);
        assertThat(result).isEqualTo(board);
        assertThat(result.getState()).isEqualTo(state);
        assertThat(result.getTimestamp()).isGreaterThan(0);
        assertThat(historySaved.get()).isTrue();
    }

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        public BoardService boardService() {
            return new BoardService();
        }
    }
}
