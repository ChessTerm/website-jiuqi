package com.chessterm.website.jiuqi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Role {

    private final boolean read;

    private final boolean write;
}
