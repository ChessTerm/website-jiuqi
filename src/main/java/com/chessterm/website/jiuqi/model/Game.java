package com.chessterm.website.jiuqi.model;

import com.chessterm.website.jiuqi.parser.StateParser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String rectpaper;

    private String initialState;

    private String availablePositions;

    @Column(name = "`row`", nullable = false)
    private int row;

    @Column(name = "`column`", nullable = false)
    private int column;

    public Game(String title, int row, int column) {
        this.title = title;
        this.row = row;
        this.column = column;
    }

    public State getInitialState() {
        if (initialState == null) {
            return StateParser.defaultState(row, column);
        } else return StateParser.toState(initialState, row);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        return id == game.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
