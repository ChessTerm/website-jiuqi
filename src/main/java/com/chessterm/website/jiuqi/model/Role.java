package com.chessterm.website.jiuqi.model;

import lombok.Getter;

@Getter
public class Role {

    private final boolean read;
    private final boolean write;

    public Role(boolean read, boolean write) {
        this.read = read;
        this.write = write;
    }
}
