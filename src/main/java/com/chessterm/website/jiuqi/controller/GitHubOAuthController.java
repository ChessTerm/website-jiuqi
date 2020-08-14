package com.chessterm.website.jiuqi.controller;

import com.chessterm.website.jiuqi.model.ReturnData;
import com.chessterm.website.jiuqi.model.User;
import com.chessterm.website.jiuqi.service.oauth.GitHubOAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RequestMapping("/oauth/github")
@RestController
public class GitHubOAuthController {

    @Autowired
    GitHubOAuthProvider provider;

    @GetMapping("/authorize")
    public void authorize(@RequestParam(value = "scope", defaultValue = "") String scope,
                          HttpServletResponse response) throws IOException {
        response.sendRedirect(provider.getAuthorizeUrl(scope));
    }

    @RequestMapping("/callback")
    public ReturnData callback(@RequestParam(name = "create", defaultValue = "no") String create,
                               @RequestParam(name = "code", defaultValue = "") String code,
                               HttpServletResponse response, HttpSession session,
                               @AuthenticationPrincipal User user) {
        boolean isCreate = create.equals("yes");
        String token = (String) session.getAttribute("github_token");
        if (token == null || token.equals("")) token = provider.requestToken(code);
        try {
            boolean successful = provider.saveToken(token, user, isCreate);
            session.setAttribute("github_token", token);
            return new ReturnData(true, successful);
        } catch (IOException e) {
            e.printStackTrace();
            session.removeAttribute("github_token");
            return new ReturnData(false);
        }
    }
}
