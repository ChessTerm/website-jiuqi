package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.StateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface StateRepository extends JpaRepository<StateHistory, Long> {

    List<StateHistory> findAllByBoardId(long id);

    StateHistory findFirstByBoardIdOrderByTimestampDesc(long id);

    @Transactional
    void deleteByBoardId(long id);
}
