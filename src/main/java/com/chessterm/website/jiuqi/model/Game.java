package com.chessterm.website.jiuqi.model;

import com.chessterm.website.jiuqi.service.StateParser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
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

    @Column(name="`row`", nullable = false)
    private int row;

    @Column(name="`column`", nullable = false)
    private int column;

    public State getInitialState() {
        if (initialState == null) {
            return StateParser.defaultState(row, column);
        } else return StateParser.toState(initialState, row);
    }
}
