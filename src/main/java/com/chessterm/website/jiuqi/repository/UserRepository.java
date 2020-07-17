package com.chessterm.website.jiuqi.repository;

import com.chessterm.website.jiuqi.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findById(long id);
}
