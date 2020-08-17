package com.chessterm.website.jiuqi.model;

import com.chessterm.website.jiuqi.parser.StateParser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Board {

    @Column(nullable = false)
    String state;

    @Id
    @GeneratedValue
    private long id;

    @OneToOne
    private Game game;

    @OneToOne
    private User user;

    @Column(nullable = false)
    private long timestamp;

    public Board(Game game, User user) {
        this.game = game;
        this.user = user;
        this.setState(game.getInitialState());
        this.timestamp = System.currentTimeMillis();
    }

    public State getState() {
        return StateParser.toState(state, game.getRow());
    }

    public void setState(State state) {
        this.state = StateParser.toString(state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        return id == board.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
