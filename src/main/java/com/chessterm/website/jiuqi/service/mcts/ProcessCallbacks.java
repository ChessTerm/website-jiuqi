package com.chessterm.website.jiuqi.service.mcts;

import com.chessterm.website.jiuqi.model.State;

import java.util.function.Consumer;

public class ProcessCallbacks {

    final Consumer<String> onFailure;

    final Consumer<State> onSuccess;

    final Consumer<Progress> onProgress;

    public ProcessCallbacks(Consumer<String> onFailure,
                            Consumer<State> onSuccess,
                            Consumer<Progress> onProgress) {
        this.onFailure = onFailure;
        this.onSuccess = onSuccess;
        this.onProgress = onProgress;
    }
}
