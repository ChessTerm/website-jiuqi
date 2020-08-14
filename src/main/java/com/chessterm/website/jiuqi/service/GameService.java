package com.chessterm.website.jiuqi.service;

import com.chessterm.website.jiuqi.model.Game;
import com.chessterm.website.jiuqi.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    @Autowired
    GameRepository repository;

    public Game get(int id) {
        return repository.findById(id);
    }
}
