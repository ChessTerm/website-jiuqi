package com.chessterm.website.jiuqi.service;

import com.chessterm.website.jiuqi.model.*;
import com.chessterm.website.jiuqi.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    @Autowired
    BoardRepository repository;

    @Autowired
    StateHistoryService historyService;

    public Board get(long id) {
        return repository.findById(id);
    }

    public Board get(long userId, int gameId) {
        return repository.findByUserIdAndGameId(userId, gameId);
    }

    public Board get(User user, Game game, boolean create) {
        Board board = get(user.getId(), game.getId());
        if (board == null && create) board = create(user, game);
        return board;
    }

    public Board create(User user, Game game) {
        Board board = new Board(game, user);
        return repository.save(board);
    }

    public Role getRole(Board board, User user) {
        User owner = board.getUser();
        if (owner.getPassword() == null || owner.getPassword().isEmpty()) {
            return new Role(true, true);
        } else if (user != null) {
            if (user.getId() == owner.getId()) {
                return new Role(true, true);
            } else if (user.isAdmin())
                return new Role(true, true);
        }
        return new Role(true, false);
    }

    public Board setState(Board board, State state) {
        board.setTimestamp(System.currentTimeMillis());
        board.setState(state);
        board = repository.save(board);
        historyService.save(board, state);
        return board;
    }

    public Board resetState(Board board) {
        return setState(board, board.getGame().getInitialState());
    }
}
