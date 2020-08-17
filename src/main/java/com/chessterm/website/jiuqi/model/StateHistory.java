package com.chessterm.website.jiuqi.model;

import com.chessterm.website.jiuqi.parser.StateParser;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StateHistory {

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

    public StateHistory(Board board) {
        this.board = board;
        this.state = board.state;
        this.timestamp = board.getTimestamp();
    }

    @JsonValue
    public State getState() {
        return StateParser.toState(state, board.getGame().getRow());
    }

    public void setState(State state) {
        this.state = StateParser.toString(state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateHistory history = (StateHistory) o;

        return id == history.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
