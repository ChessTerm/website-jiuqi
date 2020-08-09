package com.chessterm.website.jiuqi.controller;

import com.chessterm.website.jiuqi.model.*;
import com.chessterm.website.jiuqi.repository.BoardRepository;
import com.chessterm.website.jiuqi.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/boards")
@RestController
public class BoardController {

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    StateRepository stateRepository;

    @GetMapping("/{id}/role")
    public ReturnData getRole(@PathVariable long id, @AuthenticationPrincipal User user) {
        Board board = boardRepository.findById(id);
        if (board != null) {
            return new ReturnData(true, getRole(board, user));
        } else return new ReturnData(false, "Board not found.");
    }

    @GetMapping("/find")
    public ReturnData find(@RequestParam(value = "user", defaultValue = "0") long userId,
                           @RequestParam("game") int gameId, @AuthenticationPrincipal User user) {
        if (userId == 0) if (user != null) userId = user.getId();
        Board board = boardRepository.findByUserIdAndGameId(userId, gameId);
        if (board != null) {
            return new ReturnData(true, board);
        } else return new ReturnData(false, "Board not found.");
    }

    @GetMapping("/{id}/history")
    public ReturnData getHistory(@PathVariable long id) {
        Board board = boardRepository.findById(id);
        if (board != null) {
            List<State> stateList = new ArrayList<>();
            List<StateHistory> historyList = stateRepository.findAllByBoardId(board.getId());
            for (StateHistory history: historyList) {
                stateList.add(history.getState());
            }
            return new ReturnData(true, stateList);
        } else return new ReturnData(false, "Board not found.");
    }

    @MessageMapping("/boards/{id}/sync")
    @SendTo("/topic/boards/{id}/sync")
    public State sync(@DestinationVariable("id") long id,
                      Authentication authentication, State state) {
        User user = (User) authentication.getPrincipal();
        Board board = boardRepository.findById(id);
        if (board == null) {
            return null;
        } else {
            Role role = getRole(board, user);
            if (role.isWrite()) board = updateState(board, state);
            return board.getState();
        }
    }

    @MessageMapping("/boards/{id}/reset")
    @SendTo("/topic/boards/{id}/sync")
    public State reset(@DestinationVariable("id") long id,
                       Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Board board = boardRepository.findById(id);
        if (board == null) {
            return null;
        } else {
            Role role = getRole(board, user);
            if (role.isWrite())
                board = updateState(board, board.getGame().getInitialState());
            return board.getState();
        }
    }

    private Role getRole(Board board, User user) {
        User owner = board.getUser();
        if (owner.getPassword() == null || owner.getPassword().isEmpty()) {
            return new Role(true, true);
        } else if (user != null) {
            if (user.getId() == owner.getId()) {
                return new Role(true, true);
            } else for (GrantedAuthority authority: user.getAuthorities()) {
                if (authority.getAuthority().equals("admin"))
                    return new Role(true, true);
            }
        }
        return new Role(true, false);
    }

    protected Board updateState(Board board, State state) {
        long time = System.currentTimeMillis();
        board.setState(state);
        board.setTimestamp(time);
        board = boardRepository.save(board);
        StateHistory lastHistory = stateRepository.findFirstByBoardIdOrderByTimestampDesc(board.getId());
        if (!(lastHistory != null && lastHistory.getState().equals(state))) {
            if (state.equals(board.getGame().getInitialState()))
                stateRepository.deleteByBoardId(board.getId());
            StateHistory history = new StateHistory(board);
            stateRepository.save(history);
        }
        return board;
    }
}
