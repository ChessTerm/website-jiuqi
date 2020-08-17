package com.chessterm.website.jiuqi.handler;

import com.chessterm.website.jiuqi.model.ReturnData;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String account = authentication.getName();
        HttpSession session = request.getSession();
        session.setAttribute("user", account);
        ReturnData result = new ReturnData(true);
        result.writeResponse(response);
    }
}
