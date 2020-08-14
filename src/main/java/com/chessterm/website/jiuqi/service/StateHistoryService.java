package com.chessterm.website.jiuqi.service;

import com.chessterm.website.jiuqi.model.Board;
import com.chessterm.website.jiuqi.model.State;
import com.chessterm.website.jiuqi.model.StateHistory;
import com.chessterm.website.jiuqi.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateHistoryService {

    @Autowired
    StateRepository repository;

    public List<StateHistory> get(Board board) {
        return repository.findAllByBoardId(board.getId());
    }

    public void save(Board board, State state) {
        StateHistory lastHistory = getLast(board);
        if (!(lastHistory != null && lastHistory.getState().equals(state))) {
            if (state.equals(board.getGame().getInitialState()))
                repository.deleteByBoardId(board.getId());
            StateHistory history = new StateHistory(board);
            repository.save(history);
        }
    }

    public StateHistory getLast(Board board) {
        return repository.findFirstByBoardIdOrderByTimestampDesc(board.getId());
    }
}
