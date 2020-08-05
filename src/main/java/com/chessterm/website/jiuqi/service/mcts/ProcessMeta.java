package com.chessterm.website.jiuqi.service.mcts;

import com.chessterm.website.jiuqi.model.User;
import lombok.Getter;

@Getter
public class ProcessMeta {

    private final User user;

    private final Params params;

    private final ProcessCallbacks callbacks;

    private final long startTime;

    ProcessMeta(User user, Params params, ProcessCallbacks callbacks) {
        this.user = user;
        this.params = params;
        this.callbacks = callbacks;
        this.startTime = System.currentTimeMillis();
    }
}
