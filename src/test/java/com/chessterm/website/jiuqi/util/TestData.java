package com.chessterm.website.jiuqi.util;

import com.chessterm.website.jiuqi.model.Game;
import com.chessterm.website.jiuqi.model.State;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;

public class TestData {

    public static Game testGame() {
        return new Game("TEST", 14, 14);
    }

    public static State randomState() {
        Game game = testGame();
        byte[] stateArray = new byte[]{};
        byte[] stateEnum = new byte[]{-1, 0, 1};
        int count = game.getRow() * game.getColumn();
        for (int i = 0; i < count; i++) {
            byte cell = stateEnum[RandomUtils.nextInt(0, stateEnum.length)];
            stateArray = ArrayUtils.add(stateArray, cell);
        }
        return new State(stateArray, game.getColumn());
    }
}
