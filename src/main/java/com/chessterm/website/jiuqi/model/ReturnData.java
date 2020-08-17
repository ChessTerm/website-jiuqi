package com.chessterm.website.jiuqi.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

public class ReturnData {

    public final boolean success;

    public final String message;

    public final Object data;

    public ReturnData(boolean success) {
        this(success, "", null);
    }

    public ReturnData(boolean success, String message) {
        this(success, message, null);
    }

    public ReturnData(boolean success, Object data) {
        this(success, "", data);
    }

    public ReturnData(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "\"error\"";
        }
    }

    public void writeResponse(HttpServletResponse response) throws IOException {
        writeResponse(response, HttpServletResponse.SC_OK);
    }

    public void writeResponse(HttpServletResponse response, int status) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        PrintWriter writer = response.getWriter();
        writer.write(this.toJson());
        writer.flush();
        writer.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnData that = (ReturnData) o;
        if (success != that.success) return false;
        if (!Objects.equals(message, that.message)) return false;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = (success ? 1 : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
