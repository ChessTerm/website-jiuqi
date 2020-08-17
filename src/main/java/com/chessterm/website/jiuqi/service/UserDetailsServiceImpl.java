package com.chessterm.website.jiuqi.service;

import com.chessterm.website.jiuqi.model.User;
import com.chessterm.website.jiuqi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.empty();
        try {
            user = repository.findById(Long.valueOf(username));
        } catch (NumberFormatException ignored) {
        }
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found.");
        } else return user.get();
    }
}
