package com.chessterm.website.jiuqi.service;

import com.chessterm.website.jiuqi.model.User;
import com.chessterm.website.jiuqi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.empty();
        try {
            user = userRepository.findById(Long.valueOf(username));
        } catch (NumberFormatException ignored) {}
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found.");
        } else return user.get();
    }
}
