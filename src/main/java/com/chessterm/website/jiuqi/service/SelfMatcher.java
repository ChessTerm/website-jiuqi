package com.chessterm.website.jiuqi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelfMatcher implements RequestMatcher {

    @Autowired
    UserService userService;

    @Override
    public boolean matches(HttpServletRequest request) {
        String method = request.getMethod();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority authority: authentication.getAuthorities()) {
            if (authority.getAuthority().equals("admin")) return true;
        }
        if (method.equals("GET") || method.equals("PUT") || method.equals("PATCH")) {
            String url = request.getRequestURI();
            String inputUserId = null;
            Pattern pattern = Pattern.compile("users/(.*)\\.?");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                inputUserId = matcher.group(1);
            } else {
                pattern = Pattern.compile("boards/(.*)\\.?");
                matcher = pattern.matcher(url);
                if (matcher.find()) inputUserId = matcher.group(1);
            }
            if (inputUserId != null) {
                if (authentication.getPrincipal().equals(inputUserId)) {
                    return true;
                } else {
                    UserDetails inputUser = userService.loadUserByUsername(inputUserId);
                    String password = inputUser.getPassword();
                    return password == null || password.isEmpty();
                }
            } else return false;
        } else return false;
    }
}
