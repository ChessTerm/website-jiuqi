package com.chessterm.website.jiuqi.service;

import com.chessterm.website.jiuqi.model.State;

import java.util.Arrays;

public class StateParser {

    public static State toState(String state, int row) {
        char[] charArray = state.toCharArray();
        byte[] newState = new byte[charArray.length];
        for (int i = 0, l = charArray.length; i < l; i++) {
            byte cell;
            switch (charArray[i]) {
                case 'X':
                    cell = -1;
                    break;
                case 'O':
                    cell = 1;
                    break;
                default:
                    cell = 0;
            }
            newState[i] = cell;
        }
        return new State(newState, row);
    }

    public static String toString(State state) {
        byte[] stateArray = state.getState();
        char[] charArray = new char[stateArray.length];
        for (int i = 0, l = stateArray.length; i < l; i++) {
            char cell;
            switch (stateArray[i]) {
                case -1:
                    cell = 'X';
                    break;
                case 1:
                    cell = 'O';
                    break;
                default:
                    cell = '-';
            }
            charArray[i] = cell;
        }
        return String.valueOf(charArray);
    }

    public static State defaultState(int row, int column) {
        byte[] newState = new byte[row * column];
        Arrays.fill(newState, (byte) 0);
        return new State(newState, row);
    }
}
