package com.chessterm.website.jiuqi.controller;

import com.chessterm.website.jiuqi.model.Board;
import com.chessterm.website.jiuqi.model.Game;
import com.chessterm.website.jiuqi.model.ReturnData;
import com.chessterm.website.jiuqi.model.User;
import com.chessterm.website.jiuqi.service.BoardService;
import com.chessterm.website.jiuqi.service.GameService;
import com.chessterm.website.jiuqi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService service;

    @Autowired
    GameService gameService;

    @Autowired
    BoardService boardService;

    @GetMapping("")
    public ReturnData getSelfInfo(@AuthenticationPrincipal User user) {
        return new ReturnData(true, user);
    }

    @GetMapping("/getBoard")
    public ReturnData getBoardInfo(@RequestParam(name = "game", defaultValue = "0") int gameId,
                                   @AuthenticationPrincipal User user) {
        Game game = gameService.get(gameId);
        if (game != null) {
            Board board = boardService.get(user, game, true);
            return new ReturnData(true, board);
        } else return new ReturnData(false, "Game not found.");
    }

    @GetMapping("/exists")
    public ReturnData exists(@RequestParam("id") long id) {
        return new ReturnData(true, service.exists(id));
    }
}
