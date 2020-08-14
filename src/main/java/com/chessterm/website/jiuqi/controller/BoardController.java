package com.chessterm.website.jiuqi.controller;

import com.chessterm.website.jiuqi.model.*;
import com.chessterm.website.jiuqi.service.BoardService;
import com.chessterm.website.jiuqi.service.StateHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/boards")
@RestController
public class BoardController {

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    BoardService service;

    @Autowired
    StateHistoryService historyService;

    @GetMapping("/{id}/role")
    public ReturnData getRole(@PathVariable long id, @AuthenticationPrincipal User user) {
        Board board = service.get(id);
        if (board != null) {
            return new ReturnData(true, service.getRole(board, user));
        } else return new ReturnData(false, "Board not found.");
    }

    @GetMapping("/find")
    public ReturnData find(@RequestParam(value = "user", defaultValue = "0") long userId,
                           @RequestParam("game") int gameId, @AuthenticationPrincipal User user) {
        if (userId == 0) if (user != null) userId = user.getId();
        Board board = service.get(userId, gameId);
        if (board != null) {
            return new ReturnData(true, board);
        } else return new ReturnData(false, "Board not found.");
    }

    @GetMapping("/{id}/history")
    public ReturnData getHistory(@PathVariable long id) {
        Board board = service.get(id);
        if (board != null) {
            return new ReturnData(true, historyService.get(board));
        } else return new ReturnData(false, "Board not found.");
    }

    @MessageMapping("/boards/{id}/sync")
    @SendTo("/topic/boards/{id}/sync")
    public State sync(Authentication authentication, State state,
                      @DestinationVariable("id") long id) {
        User user = (User) authentication.getPrincipal();
        Board board = service.get(id);
        if (board == null) {
            return null;
        } else {
            Role role = service.getRole(board, user);
            if (role.isWrite()) board = service.setState(board, state);
            return board.getState();
        }
    }

    @MessageMapping("/boards/{id}/reset")
    @SendTo("/topic/boards/{id}/sync")
    public State reset(@DestinationVariable("id") long id,
                       Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Board board = service.get(id);
        if (board == null) {
            return null;
        } else {
            Role role = service.getRole(board, user);
            if (role.isWrite()) board = service.resetState(board);
            return board.getState();
        }
    }
}
