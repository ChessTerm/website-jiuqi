package com.chessterm.website.jiuqi.controller;

import com.chessterm.website.jiuqi.model.*;
import com.chessterm.website.jiuqi.repository.BoardRepository;
import com.chessterm.website.jiuqi.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BoardController {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    StateRepository stateRepository;

    @GetMapping("/boards/{id}/role")
    public ReturnData getRole(@PathVariable long id) {
        Board board = boardRepository.findById(id);
        if (board != null) {
            return new ReturnData(true, getRole(board));
        } else return new ReturnData(false, "Board not found.");
    }

    @GetMapping("/boards/find")
    public ReturnData find(@RequestParam(value = "user", defaultValue = "0") long userId,
                           @RequestParam("game") int gameId) {
        if (userId == 0) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                userId = ((User) authentication.getPrincipal()).getId();
            }
        }
        Board board = boardRepository.findByUserIdAndGameId(userId, gameId);
        if (board != null) {
            return new ReturnData(true, board);
        } else return new ReturnData(false, "Board not found.");
    }

    @MessageMapping("/boards/{id}/sync")
    @SendTo("/topic/boards/{id}/sync")
    public State sync(@DestinationVariable("id") long id, State state) {
        Board board = boardRepository.findById(id);
        if (board == null) {
            return null;
        } else {
            Role role = getRole(board);
            if (role.isWrite()) {
                board = updateState(board, state);
            }
            return board.getState();
        }
    }

    private Role getRole(Board board) {
        User owner = board.getUser();
        if (owner.getPassword() == null || owner.getPassword().isEmpty()) {
            return new Role(true, true);
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken) &&
                ((User) authentication.getPrincipal()).getId() == owner.getId()) {
                return new Role(true, true);
            } else return new Role(true, false);
        }
    }

    private Board updateState(Board board, State state) {
        long time = System.currentTimeMillis();
        board.setState(state);
        if (state.equals(board.getGame().getInitialState()))
            stateRepository.deleteAllByBoardId(board.getId());
        board.setTimestamp(time);
        board = boardRepository.save(board);
        StateHistory history = new StateHistory(board);
        stateRepository.save(history);
        return board;
    }
}
