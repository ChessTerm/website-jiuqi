package com.chessterm.website.jiuqi.model;

import com.chessterm.website.jiuqi.parser.StateParser;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StateHistory {

    public StateHistory() {
    }

    public StateHistory(Board board) {
        this.board = board;
        this.state = board.state;
        this.timestamp = board.getTimestamp();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state_history_sequence")
    @SequenceGenerator(name = "state_history_sequence", allocationSize = 1)
    private long id;

    @OneToOne
    private Board board;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private long timestamp;

    @JsonValue
    public State getState() {
        return StateParser.toState(state, board.getGame().getRow());
    }

    public void setState(State state) {
        this.state = StateParser.toString(state);
    }
}
