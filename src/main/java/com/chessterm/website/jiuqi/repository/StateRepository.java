package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.StateHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StateRepository extends CrudRepository<StateHistory, Long> {

    List<StateHistory> findAllByBoardId(long id);

    void deleteAllByBoardId(long id);
}
