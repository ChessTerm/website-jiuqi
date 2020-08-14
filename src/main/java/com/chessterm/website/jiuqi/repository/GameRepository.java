package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.Game;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameRepository extends PagingAndSortingRepository<Game, Integer> {

    Game findById(int id);
}
