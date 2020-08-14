package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.Board;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BoardRepository extends PagingAndSortingRepository<Board, Long> {

    Board findById(long id);

    Board findByUserIdAndGameId(long userId, int gameId);
}
