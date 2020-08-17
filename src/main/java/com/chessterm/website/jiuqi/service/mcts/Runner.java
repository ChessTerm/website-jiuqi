package com.chessterm.website.jiuqi.service.mcts;

import com.chessterm.website.jiuqi.model.State;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingbh.flamechess.jiuqi.Game;
import com.jingbh.flamechess.jiuqi.JiuqiState;
import com.jingbh.flamechess.mcts.Tree;

import java.io.IOException;

public class Runner {

    private static final int maxNode = 1000;

    private static final int maxDepth = Integer.MAX_VALUE;

    private static final int threads = 1;

    public static State run(Params params) {
        Game game = new Game();
        JiuqiState state = new JiuqiState(params.state.get2dState(), params.stage);
        Tree tree = new Tree(state, game, params.player,
            maxNode, maxDepth, 1, true);
        com.jingbh.flamechess.State result = tree.toolBoxHelper();
        return new State(result);
    }

    public static void main(String[] args) {
        try {
            Params params = new ObjectMapper().readValue(System.in, Params.class);
            State newState = run(params);
            new ObjectMapper().writeValue(System.out, newState);
            System.exit(0);
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
