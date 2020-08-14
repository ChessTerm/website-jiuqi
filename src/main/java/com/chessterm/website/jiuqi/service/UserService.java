package com.chessterm.website.jiuqi.service;

import com.chessterm.website.jiuqi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository repository;

    public boolean exists(long id) {
        return repository.existsById(id);
    }
}
