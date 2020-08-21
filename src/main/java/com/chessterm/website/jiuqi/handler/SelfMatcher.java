package com.chessterm.website.jiuqi.handler;

import com.chessterm.website.jiuqi.model.Board;
import com.chessterm.website.jiuqi.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelfMatcher implements RequestMatcher {

    @Autowired
    @Qualifier("customUserService")
    UserDetailsService userDetailsService;

    @Autowired
    BoardService boardService;

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
                pattern = Pattern.compile("boards/([0-9]*)");
                matcher = pattern.matcher(url);
                if (matcher.find()) {
                    Board board = boardService.get(Integer.parseInt(matcher.group(1)));
                    if (board != null) inputUserId = String.valueOf(board.getUser().getId());
                }
            }
            if (inputUserId != null) {
                if (authentication.getPrincipal().equals(inputUserId)) {
                    return true;
                } else {
                    UserDetails inputUser = userDetailsService.loadUserByUsername(inputUserId);
                    String password = inputUser.getPassword();
                    return password == null || password.isEmpty();
                }
            } else return false;
        } else return false;
    }
}
