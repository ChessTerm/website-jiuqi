package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.*;
import com.chessterm.website.jiuqi.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class StateRepositoryTest {

    private static Board board;

    @Autowired
    private TestEntityManager manager;

    @Autowired
    private StateRepository repository;

    @BeforeEach
    private void setupBoard() {
        manager.clear();
        User user = manager.persist(new User(10000));
        Game game = manager.persist(TestData.testGame());
        board = manager.persist(new Board(game, user));
        manager.flush();
    }

    @Test
    public void saveState() {
        State state = TestData.randomState();
        board.setState(state);
        StateHistory entity = manager.persist(new StateHistory(board));
        assertThat(entity.getState()).isEqualTo(state);
        assertThat(entity.getTimestamp()).isGreaterThan(0);
    }

    @Test
    public void findByBoard() {
        board.setState(TestData.randomState());
        board.setTimestamp(0);
        StateHistory entity1 = manager.persist(new StateHistory(board));
        board.setState(TestData.randomState());
        board.setTimestamp(1);
        StateHistory entity2 = manager.persist(new StateHistory(board));
        List<StateHistory> result1 = repository.findAllByBoardId(board.getId());
        assertThat(result1.size()).isEqualTo(2);
        assertThat(result1.contains(entity1)).isTrue();
        assertThat(result1.contains(entity2)).isTrue();
        StateHistory result2 = repository.findFirstByBoardIdOrderByTimestampDesc(board.getId());
        assertThat(result2).isEqualTo(entity2);
        repository.deleteByBoardId(board.getId());
        List<StateHistory> result3 = repository.findAllByBoardId(board.getId());
        assertThat(result3.size()).isEqualTo(0);
    }
}
