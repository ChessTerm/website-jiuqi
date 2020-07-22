package com.chessterm.website.jiuqi.model;

import com.chessterm.website.jiuqi.service.StateParser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StateHistory {

    public StateHistory() {}

    public StateHistory(Board board) {
        this.board = board;
        this.state = board.state;
        this.timestamp = board.getTimestamp();
    }

    @Id
    @GeneratedValue
    private long id;

    @OneToOne
    private Board board;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private long timestamp;

    public State getState() {
        return StateParser.toState(state, board.getGame().getRow());
    }

    public void setState(State state) {
        this.state = StateParser.toString(state);
    }
}
