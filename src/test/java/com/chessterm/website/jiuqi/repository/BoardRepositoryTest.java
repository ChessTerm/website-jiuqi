package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.Board;
import com.chessterm.website.jiuqi.model.Game;
import com.chessterm.website.jiuqi.model.User;
import com.chessterm.website.jiuqi.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BoardRepositoryTest {

    private static User user;

    private static Game game;

    private static Board board;

    @Autowired
    private TestEntityManager manager;

    @Autowired
    private BoardRepository repository;

    @BeforeEach
    private void setupBoard() {
        manager.clear();
        user = manager.persist(new User(10000));
        game = manager.persist(TestData.testGame());
        board = manager.persist(new Board(game, user));
        manager.flush();
    }

    @Test
    public void createBoard() {
        Board result = repository.findByUserIdAndGameId(user.getId(), game.getId());
        assertThat(result).isEqualTo(board);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getGame()).isEqualTo(game);
        assertThat(result.getState()).isEqualTo(game.getInitialState());
        assertThat(result.getTimestamp()).isGreaterThan(0);
    }
}
