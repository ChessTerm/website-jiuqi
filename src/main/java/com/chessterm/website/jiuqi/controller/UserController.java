package com.chessterm.website.jiuqi.controller;

import com.chessterm.website.jiuqi.model.Board;
import com.chessterm.website.jiuqi.model.Game;
import com.chessterm.website.jiuqi.model.ReturnData;
import com.chessterm.website.jiuqi.model.User;
import com.chessterm.website.jiuqi.repository.BoardRepository;
import com.chessterm.website.jiuqi.repository.GameRepository;
import com.chessterm.website.jiuqi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    BoardRepository boardRepository;

    @GetMapping("")
    public ReturnData getSelfInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ReturnData(true, authentication.getPrincipal());
    }

    @GetMapping("/getBoard")
    public ReturnData getBoardInfo(@RequestParam(name = "game", defaultValue = "0") int gameId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Game game = gameRepository.findById(gameId);
        if (game != null) {
            Board board = boardRepository.findByUserIdAndGameId(user.getId(), game.getId());
            if (board == null) {
                board = new Board(game, user);
                board = boardRepository.save(board);
            }
            return new ReturnData(true, board);
        } else return new ReturnData(false, "Please specify a valid gameId.");
    }

    @GetMapping("/exists")
    public ReturnData exists(@RequestParam("id") long id) {
        return new ReturnData(true, userRepository.existsById(id));
    }
}
