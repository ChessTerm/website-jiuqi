package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.Board;
import org.springframework.data.repository.CrudRepository;

public interface BoardRepository extends CrudRepository<Board, Long> {

    Board findById(long id);

    Board findByUserIdAndGameId(long userId, int gameId);
}
