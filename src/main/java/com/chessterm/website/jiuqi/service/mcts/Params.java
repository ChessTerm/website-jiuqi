package com.chessterm.website.jiuqi.service.mcts;

import com.chessterm.website.jiuqi.model.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingbh.flamechess.jiuqi.Stage;

import java.io.Serializable;

public class Params implements Serializable {

    public State state;

    public byte player;

    public Stage stage;

    /**
     * Empty constructor for deserialization.
     */
    public Params() {
    }

    public Params(State state, byte player, Stage stage) {
        this.state = state;
        this.player = player;
        this.stage = stage;
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public static Params fromJson(String string) throws JsonProcessingException {
        return new ObjectMapper().readValue(string, Params.class);
    }
}
