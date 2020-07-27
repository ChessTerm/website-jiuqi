package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.StateHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StateRepository extends CrudRepository<StateHistory, Long> {

    List<StateHistory> findAllByBoardId(long id);

    StateHistory findFirstByBoardIdOrderByTimestampDesc(long id);

    @Transactional
    void deleteByBoardId(long id);
}
