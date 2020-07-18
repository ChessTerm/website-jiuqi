package com.chessterm.website.jiuqi.controller;

import com.chessterm.website.jiuqi.model.ReturnData;
import com.chessterm.website.jiuqi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user")
    public ReturnData getSelfInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ReturnData(true, authentication.getPrincipal());
    }
}
