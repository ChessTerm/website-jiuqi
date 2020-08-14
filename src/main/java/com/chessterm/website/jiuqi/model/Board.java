package com.chessterm.website.jiuqi.model;

import com.chessterm.website.jiuqi.parser.StateParser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Board {

    public Board() {
    }

    public Board(Game game, User user) {
        this.game = game;
        this.user = user;
        this.setState(game.getInitialState());
        this.timestamp = System.currentTimeMillis();
    }

    @Id
    @GeneratedValue
    private long id;

    @OneToOne
    private Game game;

    @OneToOne
    private User user;

    @Column(nullable = false)
    String state;

    @Column(nullable = false)
    private long timestamp;

    public State getState() {
        return StateParser.toState(state, game.getRow());
    }

    public void setState(State state) {
        this.state = StateParser.toString(state);
    }
}
