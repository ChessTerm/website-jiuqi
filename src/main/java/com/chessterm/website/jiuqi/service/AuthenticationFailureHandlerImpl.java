package com.chessterm.website.jiuqi.service;

import com.chessterm.website.jiuqi.model.ReturnData;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        ReturnData result = new ReturnData(false, "Login failed.");
        result.writeResponse(response, HttpServletResponse.SC_UNAUTHORIZED);
    }
}
