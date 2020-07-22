package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Integer> {

    Game findById(int id);
}
