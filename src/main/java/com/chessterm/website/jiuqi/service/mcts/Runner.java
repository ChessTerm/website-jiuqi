package com.chessterm.website.jiuqi.service.mcts;

import com.chessterm.website.jiuqi.model.State;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingbh.flamechess.jiuqi.Game;
import com.jingbh.flamechess.jiuqi.mcts.Tree;
import lombok.Getter;

import java.io.IOException;

public class Runner {

    private static final int maxNode = 1000;
    private static final int maxDepth = Integer.MAX_VALUE;
    private static final int threads = 1;

    public static State run(Params params) {
        Game game = new Game();
        Tree tree = new Tree(params.state, game, params.player,
            maxNode, maxDepth, params.stage, threads, true);
        byte[][] result = tree.toolBoxHelper().get2dState();
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

    @Getter
    public static class Progress {

        private final int now;

        private final int all;

        public Progress(int now, int all) {
            this.now = now;
            this.all = all;
        }

        public double getPercentage() {
            return ((double) now) / ((double) all);
        }
    }
}
