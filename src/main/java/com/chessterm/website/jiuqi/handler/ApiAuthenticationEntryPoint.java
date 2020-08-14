package com.chessterm.website.jiuqi.handler;

import com.chessterm.website.jiuqi.model.ReturnData;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ReturnData result = new ReturnData(false, "Please login first.");
        result.writeResponse(response, HttpServletResponse.SC_UNAUTHORIZED);
    }
}
