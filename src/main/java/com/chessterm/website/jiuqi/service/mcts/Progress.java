package com.chessterm.website.jiuqi.service.mcts;

import lombok.Getter;

@Getter
public class Progress {

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
