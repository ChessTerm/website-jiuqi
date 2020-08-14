package com.chessterm.website.jiuqi.handler;

import com.chessterm.website.jiuqi.model.ReturnData;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        ReturnData result = new ReturnData(false, "Access denied.");
        result.writeResponse(response, HttpServletResponse.SC_FORBIDDEN);
    }
}
